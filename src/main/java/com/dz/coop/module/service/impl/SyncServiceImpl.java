package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookPropertyRpc;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.rpc.ShortStoryReleaseRpc;
import com.dz.content.api.book.vo.*;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.*;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.ChapterModifySyncTypeEnum;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.constant.UpdateBookTypeEnum;
import com.dz.coop.module.mapper.*;
import com.dz.coop.module.model.*;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.model.vo.SaveChapterResp;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.support.BookSupport;
import com.dz.coop.module.support.ChapterSupport;
import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.glory.common.tools.JsonUtil;
import com.dz.glory.common.vo.Ret;
import com.dz.rocketmq.producer.RocketMQProducer;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author panqz 2018-10-29 10:43 AM
 */
@Service
public class SyncServiceImpl implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);

    @Resource
    private BookService bookService;

    @Resource
    private VolumeService volumeService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private PartnerService partnerService;

    @Resource
    private MsgService msgService;

    @Resource
    private ChapterCoverHisMapper chapterCoverHisMapper;

    @Resource
    private AuditBookMapper auditBookMapper;

    @Resource
    private CheckBookMapper checkBookMapper;

    @Resource
    private BookBreakChapterSyncService bookBreakChapterSyncService;

    @Resource
    private ExceptionBookRecordService exceptionBookRecordService;

    @Resource
    private AudioTrailerService audioTrailerService;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Reference
    private ShortStoryReleaseRpc shortStoryReleaseRpc;

    @Autowired
    private BookCategoryService bookCategoryService;
    @Reference
    private AdminBookPropertyRpc adminBookPropertyRpc;

    @Autowired
    private ShortStoryService shortStoryService;

    @Resource
    private JedisClient jedisClient;

    @Override
    public void sync(CPBook cpBook) {
        String bookId = cpBook.getBookId();
        Long cpId = cpBook.getCpId();
        Partner partner = null;
        boolean needUpdateChapter = false;
        // 是否需要修改章节
        boolean needModifyChapter = false;
        boolean needModifyCover = false;
        AtomicLong totalWordSize = new AtomicLong();

        try {
            if (StringUtils.isBlank(bookId)) {
                bookId = bookService.saveBook(cpBook);
            }

            String cover = cpBook.getCover();
            if (!BookSupport.existCover(bookId)) {
                boolean downCoverResult = BookSupport.downImg(bookId, cover);
                if (Boolean.FALSE.equals(downCoverResult)) {
                    needModifyCover = true;
                }
            }

            String prefix = String.format("[%s]", bookId);

            List<CPVolume> volumes = cpBook.getCpVolumeList();
            if (CollectionUtils.isEmpty(volumes)) {
                throw new BookException("{} 卷列表为空", prefix);
            }

            partner = partnerService.getPartnerById(cpId);
            if (partner == null) {
                throw new BookException("{} cpId={}三方cp不存在", prefix, cpId.toString());
            }

            boolean innerDz = partner.isInnerDz();
            boolean isUpdate = false;

            //本地多出来的章节执行覆盖处理
            BookChapterQueryRequest request = new BookChapterQueryRequest();
            request.setBookId(bookId);
            request.setIsMore(1);
            request.addOrderBy("id", true);
            List<BookChapterVO> bookChapterVOs = adminBookRpc.getAllBookChapter(request).getData();
            List<Chapter> moreChapters = BeanUtil.convertList2List(bookChapterVOs, Chapter.class);
            int min = 0;
            if (!moreChapters.isEmpty()) {
                List<CPChapter> cpChapters = new ArrayList<>();
                for (CPVolume cpVolume : volumes) {
                    cpChapters.addAll(cpVolume.getChapterList());
                }
                min = Math.min(moreChapters.size(), cpChapters.size());
                for (int i = 0; i < min; i++) {
                    SaveChapterResp resp = chapterService.updateChapter(bookId, cpChapters.get(i), moreChapters.get(i), innerDz, partner);
                    if (resp != null && resp.getSize() != null) {
                        totalWordSize.addAndGet(resp.getSize());
                    }
                }

                needModifyChapter = true;
            }

            int count = 0;
            if (min > 0) {
                for (Iterator<CPVolume> iterator = volumes.iterator(); iterator.hasNext(); ) {
                    CPVolume next = iterator.next();
                    List<CPChapter> chapterList = next.getChapterList();
                    for (Iterator<CPChapter> cpChapterIterator = chapterList.iterator(); cpChapterIterator.hasNext(); ) {
                        cpChapterIterator.next();
                        count++;
                        if (count <= min) {
                            cpChapterIterator.remove();
                        } else {
                            break;
                        }
                    }
                    if (chapterList.size() == 0) {
                        iterator.remove();
                    }
                }
            }

            //章节更新处理
            String finalBookId = bookId;
            int bookType = bookService.getBookType(partner);

            for (CPVolume cpVolume : volumes) {
                Long volumeId = volumeService.saveVolums(cpVolume, finalBookId);

                List<CPChapter> chapters = cpVolume.getChapterList();
                if (CollectionUtils.isEmpty(chapters)) {
                    throw new BookException("{} volumeid={}章节列表为空", prefix, volumeId.toString());
                }

                chapters.forEach(cpChapter -> {
                    SaveChapterResp resp = chapterService.saveChapter(cpChapter, finalBookId, volumeId, innerDz, bookType, cpId);
                    if (resp != null && resp.getSize() != null) {
                        totalWordSize.addAndGet(resp.getSize());
                    }
                });

                isUpdate = true;
            }

            needUpdateChapter = isUpdate;

            // 删除异常记录
            exceptionBookRecordService.deleteExceptionBookRecord(bookId);

            // 保存中企瑞铭片花
            audioTrailerService.saveTrailers(cpId, cpBook.getAudioTrailers(), bookId);

            dealShortCpAutoRelease(cpBook, needModifyCover, bookId, totalWordSize.get());
        } catch (Exception e) {

            needUpdateChapter = true;
            logger.error("保存书籍信息失败:{}", bookId, e);
            throw new BookException(e.getMessage(), e);

        } finally {
            if (needUpdateChapter && StringUtils.isNotBlank(bookId)) {
                //发送追更消息
                Map<String, String> msg = new HashMap<>(2);
                msg.put("cpPartnerId", cpId.toString());
                msg.put("bookId", bookId);
                if (RocketMQProducer.send("topic_append_chapter", msg)) {
                    logger.info("bookid={} 追更消息队列插入成功", bookId);
                } else {
                    logger.error("bookid={} 追更消息队列插入失败", bookId);
                }

                SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_AND_CHAPTER_UPDATE.getType());
            }

            if (needModifyChapter) {
                SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType());
            }
        }
    }


    @Override
    public void cover(String bookId) throws Exception {
        cover(bookId, false);
    }

    @Override
    public void cover(String bookId, boolean isforceCover) throws Exception {
        cover(bookId, 0L, -1L, isforceCover);
    }

    @Override
    public void cover(String bookId, Long startChapterId, Long endChapterId, boolean isforceCover) throws Exception {
        String autoCover = TraceKeyHolder.getUserKey("_auto_cover");
        String autoDesc = StringUtils.equals(autoCover, "1") ? "自动" : "手动";
        String prefix = String.format("[%s覆盖][bookId=%s][startChapterId=%s][endChapterId=%s]", autoDesc, bookId, startChapterId, endChapterId);

        if (startChapterId != 0 && endChapterId != -1 && startChapterId > endChapterId) {
            logger.error("{}传入的起始章节id=[{}]大于终止章节id[{}]", prefix, startChapterId, endChapterId);
            return;
        }

        TraceKeyHolder.setUserKey("_bookId", bookId);
        TraceKeyHolder.setUserKey("prefix", prefix);
        logger.info("{} 开始覆盖", prefix);

        Book book = bookService.getBookByBookId(bookId);

        if (book == null) {
            logger.warn("{}书籍不存在", prefix);
            return;
        }

        Long cpId = Long.parseLong(book.getPartnerId());

        Partner partner = partnerService.getPartnerById(cpId);

        if (partner == null) {
            logger.warn("{}cpid={}不存在", prefix, cpId);
            return;
        }

        prefix = String.format("[%s覆盖][%s][bookId=%s]", autoDesc, partner.getName(), bookId);

        ChapterCoverHis chapterCoverHis = new ChapterCoverHis();
        chapterCoverHis.setBookId(bookId);
        BookChapterVO bookChapterVO = adminBookRpc.getLastChapter(bookId).getData();
        chapterCoverHis.setLasterChapterId(bookChapterVO != null ? bookChapterVO.getCpChapterId() : "0");
        chapterCoverHis.setStatus(ChapterCoverHis.FAIL);

        boolean canNotCover = !isforceCover && (auditBookMapper.getAuditBook(bookId) != null || checkBookMapper.getCheckBook(bookId) != null);
        if (canNotCover) {
            logger.warn("{}审核书库书籍，暂时不覆盖", prefix);
            return;
        }

        try {
            ClientService instance = ClientFactory.getInstance(cpId);
            if (instance == null) {
                logger.warn("{}cpid={}不存在", prefix, cpId);
                return;
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPVolume> cpVolumes;
            try {
                cpVolumes = instance.getVolumeList(partner, book.getCpBookId());
            } catch (Exception e) {
                throw new BookException("{}接口卷列表为空", prefix);
            }

            if (CollectionUtils.isEmpty(cpVolumes)) {
                throw new BookException("{}接口卷列表为空", prefix);
            }

            List<CPChapter> cpChapters = new ArrayList<>();
            for (CPVolume cpVolume : cpVolumes) {
                cpChapters.addAll(cpVolume.getChapterList());
            }
            if (CollectionUtils.isEmpty(cpChapters)) {
                throw new BookException("{}接口章节列表为空", prefix);
            }

            List<BookChapterVO> owchChapters = chapterService.getAllChapterByBookId(bookId);

            if (CollectionUtils.isEmpty(owchChapters)) {
                throw new BookException("{}本地章节列表为空", prefix);
            }

            // 比接口多出的部分is_more标记为1
            if (owchChapters.size() > cpChapters.size()) {

                List<BookChapterVO> bookChapterVOs = owchChapters.subList(cpChapters.size(), owchChapters.size());

                bookChapterVOs.stream().forEach(bkChapterVO -> {
                    bkChapterVO.setIsMore(1);
                    adminBookRpc.updateBookChapter(bkChapterVO);
                });

                owchChapters = owchChapters.stream().limit(cpChapters.size()).collect(Collectors.toList());
            }

            // 生成随机的三个字符用于修改cp章节id，防止覆盖时出现主键冲突
            adminBookRpc.modifyCpChapterIdByRange(bookId, startChapterId, endChapterId);

            boolean isAudio = bookService.isAudio(partner);
            // 是否是蜻蜓FM
            boolean isQingTingFM = cpId == ThirdPart.QING_TING_FM.getCpId().longValue();
            // 查询指定书籍所有原始拆分书籍（包含拆分书籍的拆分书籍）
            List<BreakChapterConf> breakChapterConfs = new ArrayList<>();
            getAllBreakChapterConfs(breakChapterConfs, bookId);

            for (int i = 0; i < owchChapters.size(); i++) {

                BookChapterVO owchChapter = owchChapters.get(i);

                Long chapterId = owchChapter.getId();

                // 只覆盖不小于传入章节id的章节内容
                if (startChapterId > 0 && chapterId < startChapterId) {
                    logger.info("{}指定章节id=[{}]小于传入的起始章节id=[{}]，该章节不执行覆盖", prefix, chapterId, startChapterId);
                    continue;
                }

                // 只覆盖不大于传入章节id的章节内容
                if (endChapterId != -1 && chapterId > endChapterId) {
                    logger.info("{}指定章节id=[{}]大于传入的终止章节id=[{}]，该章节不执行覆盖", prefix, chapterId, endChapterId);
                    continue;
                }

                CPChapter cpChapter = cpChapters.get(i);

                String cpChapterId = cpChapter.getId();
                String chapterName = null;
                String content = null;

                // 文本（当当的除外，内容已经在章节列表中，无需重复获取），有声南京萌鹿和蜻蜓FM不需要获取
                boolean needGrabContent = (!isAudio && !ThirdPart.DANG_DANG.getCpId().equals(cpId)) || (isAudio && !ThirdPart.NAN_JING_MENG_LU.getCpId().equals(partner.getId()) && !ThirdPart.QING_TING_FM.getCpId().equals(partner.getId()));

                if (needGrabContent) {
                    CPChapter chapterInfo = instance.getCPChapterInfo(partner, book.getCpBookId(), owchChapter.getCpVolumeId(), cpChapterId);

                    chapterName = StringUtils.isBlank(cpChapter.getName()) ? chapterInfo.getName() : cpChapter.getName();

                    content = chapterInfo.getContent();

                    if (StringUtil.getRealLength(content) < 20) {
                        exceptionBookRecordService.saveEmptyChapterContentRecord(bookId, cpChapterId, chapterName);
                        throw new BookException("{}[cpChapterId={}][chapterName={}]章节内容为空", prefix, cpChapterId, chapterName);
                    }

                    if (partner.getBookType() == BookTypeEnum.TXT.getType()) {
                        if (!partner.isInnerDz()) {
                            content = chapterName + "\n" + content;
                        }
                        content = EscapeUtil.escape(content);
                    } else {
                        cpChapter.setContent(content);
                    }
                    // 有声读物
                } else {
                    chapterName = cpChapter.getName();

                    // 蜻蜓FM不需要获取内容
                    if (!isQingTingFM) {
                        content = cpChapter.getContent();
                    }

                    if (ThirdPart.DANG_DANG.getCpId().equals(cpId)) {
                        content = EscapeUtil.escape(chapterName + "\n" + content);
                    }
                }

                try {
                    int contentLen;
                    if (!isAudio) {
                        if (StringUtils.isNotBlank(content) && StringUtil.containsHtml(content)) {
                            logger.error("书籍章节内容疑似包含HTML标签,bookId={},cpChapterId={},chapterName={}", bookId, cpChapterId, chapterName);
                        }

                        owchChapter.setContent(content);
                        contentLen = StringUtil.getRealLength(content);
                    } else {
                        if (!isQingTingFM) {
                            if (ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(cpId)) {
                                contentLen = cpChapter.getDuration();
                                chapterService.updateChapterExtend(bookId, chapterId.toString(), cpChapter, cpId, owchChapter);
                            } else {
                                String suffixFormat = chapterService.getChapterPathSuffix(cpChapter, cpId);
                                String audioPath = ChapterSupport.getAudioChapterPath(bookId, chapterId, suffixFormat);
                                String audioTempPath = BookSupport.getAudioLocalPath(audioPath);
                                AudioUtil.download(content, audioTempPath);
                                contentLen = ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId) ? (int) AudioUtil.getDuration(audioTempPath) : cpChapter.getDuration();
                                // 更新章节扩展表中的音频格式信息
                                if (ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId)) {
                                    chapterService.updateChapterExtend(bookId, chapterId.toString(), suffixFormat, owchChapter);
                                } else {
                                    chapterService.updateChapterExtend(bookId, chapterId.toString(), cpChapter, cpId, owchChapter);
                                }
                                // 上传音频到OSS，并删除临时文件
                                AliAudioOssUtil.setFile(audioPath, new FileInputStream(audioTempPath));
                                FileUtils.deleteQuietly(new File(audioTempPath));
                                logger.info("音频临时文件[{}]删除成功", audioTempPath);
                            }
                        } else {
                            contentLen = cpChapter.getDuration();
                        }
                    }

                    owchChapter.setName(EscapeUtil.escape(chapterName));
                    owchChapter.setCpChapterId(cpChapterId);
                    owchChapter.setWordNum(contentLen);
                    owchChapter.setIsExist(1);
                    owchChapter.setIsMore(0);
                    // 为蜻蜓FM时，则重新覆盖收费状态
                    if (isQingTingFM) {
                        owchChapter.setIsFree(cpChapter.getIsFree());
                    }

                    adminBookRpc.updateBookChapter(owchChapter);

                    // 如果拆分原始书籍章节内容，则对应的拆分书籍所对应的章节内容也需要修改
                    if (CollectionUtils.isNotEmpty(breakChapterConfs)) {
                        bookBreakChapterSyncService.modifyBreakChapterContent(bookId, chapterId, breakChapterConfs);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType(), ChapterModifySyncTypeEnum.RANGE.getType(), startChapterId, endChapterId);

            // 有对应的拆分书籍，则拆分书籍也需要同步到华为和海外
            if (CollectionUtils.isNotEmpty(breakChapterConfs)) {
                for (BreakChapterConf breakChapterConf : breakChapterConfs) {
                    SendMQMsgUtil.send(breakChapterConf.getNewBookId(), UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType());
                }
            }

            msgService.produceByBook(bookId);

            chapterCoverHis.setStatus(ChapterCoverHis.SUCCESS);

            // 删除书籍异常记录
            exceptionBookRecordService.deleteExceptionBookRecord(bookId);

        } finally {
            chapterCoverHisMapper.save(chapterCoverHis);
            logger.info("{} 覆盖结束", prefix);
            TraceKeyHolder.clear();
        }
    }

    /**
     * 递归获取原始书的所有拆分书籍
     *
     * @param breakChapterConfs
     * @param bookId
     */
    private void getAllBreakChapterConfs(List<BreakChapterConf> breakChapterConfs, String bookId) {
        List<BreakChapterConf> breakChapterConfs1 = bookBreakChapterSyncService.getBreakChapterConfByBookId(bookId);

        if (CollectionUtils.isNotEmpty(breakChapterConfs1)) {
            breakChapterConfs.addAll(breakChapterConfs1);

            for (BreakChapterConf breakChapterConf : breakChapterConfs1) {
                getAllBreakChapterConfs(breakChapterConfs, breakChapterConf.getNewBookId());
            }
        }
    }

    private boolean dealShortCpAutoRelease(CPBook cpBook, Boolean needModifyCover, String bookId, Long totalSize) {
        String partnerIdStr = jedisClient.getString(new Key("SAR", "partner"));
        if (StringUtils.isEmpty(partnerIdStr)) {
            logger.info("没有配置自动上架cp，跳过任务");
            return false;
        }
        List<Long> partnerIds = Arrays.stream(partnerIdStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        //完本书且指定cp，自动打标签上架
        if ("1".equals(cpBook.getCompleteStatus()) && totalSize < 100000 && partnerIds.contains(cpBook.getCpId())) {
            //自动补充信息
            PartnerCategoryModel categoryModel = bookCategoryService.getCategory(cpBook.getCpId(), cpBook.getCategory());
            if (categoryModel == null) {
                logger.info("短篇书籍{}未找到对应的分类信息,不能自动上架", bookId);
                return false;
            }
            DealTypeParam parseTypeVo = shortStoryService.checkNeedAddType(bookId);
            Ret<Boolean> resp = shortStoryReleaseRpc.completeShortStory(bookId, needModifyCover, parseTypeVo);
            if (resp != null && Boolean.FALSE.equals(resp.getData())) {
                logger.info("短篇书籍{}不符合上架条件", bookId);
                return false;
            }
            //上架
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("bookId", bookId.trim());
            updateMap.put("grade", 6);
            updateMap.put("marketStatus", 1);
            Ret<AdminBookVO> adminBookVORet = adminBookRpc.updateBookInfo(updateMap);
            if (adminBookVORet != null && adminBookVORet.getData() != null && adminBookVORet.success()) {
                logger.info("短篇书籍{}自动上架成功", bookId);
                Partner partner = partnerService.getPartnerById(cpBook.getCpId());
                String cpName = "【" + partner.getName() + "】";
                shortStoryReleaseRpc.sendRemind(bookId, cpBook.getName(),cpName);
                return true;
            } else {
                logger.info("短篇书籍{}自动上架失败,响应信息:{}", bookId, JsonUtil.obj2Json(adminBookVORet));
            }
        }
        return false;
    }

}

