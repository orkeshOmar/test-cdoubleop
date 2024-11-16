package com.dz.coop.module.service.impl;

import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.BookMsg;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.support.BookSupport;
import com.dz.tools.JsonUtil;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * @author panqz 2018-11-27 1:30 PM
 */
@Service
public class ConsumeServiceImpl implements ConsumeService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumeServiceImpl.class);

    @Resource
    private MailService mailService;

    @Resource
    private SyncService syncService;

    @Resource
    private ExceptionBookRecordService exceptionBookRecordService;

    @Resource
    private AudioTrailerService audioTrailerService;

    @Autowired
    private ShortStoryService shortStoryService;

    @Override
    public void accept(String s) {

        if (StringUtils.isBlank(s)) {
            logger.warn("msg is blank");
            return;
        }

        try {

            BookMsg msg = JsonUtil.readValue(s, BookMsg.class);

            String bookId = msg.getBookId();
            String cpBookId = msg.getCpBookId();
            String lastChapterId = msg.getLastChapterId();
            String status = msg.getStatus();
            Partner partner = msg.getPartner();
            Long cpId = partner.getId();

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            String prefix = String.format("[%s][%s][%s]", TraceKeyHolder.getUserKey("prefix"), partner.getName(), StringUtils.isNotBlank(bookId) ? bookId + "->" + cpBookId : cpBookId);
            TraceKeyHolder.setUserKey("prefix", prefix);
            TraceKeyHolder.setUserKey("_bookId", StringUtils.isNotBlank(bookId) ? bookId : "");

            ClientService clientService = ClientFactory.getInstance(cpId);

            CPBook bookInfo = clientService.getBookInfo(partner, cpBookId);
            if (StringUtils.isNotBlank(bookId)) {
                bookInfo.setBookId(bookId);
            }
            if (StringUtils.isNotBlank(status)) {
                bookInfo.setCompleteStatus(status);
            }
            bookInfo.setCpId(partner.getId());

            // 非新书检查封面是否已经下载，没有则下载
            if (StringUtils.isNotBlank(bookId) && !BookSupport.existCover(bookId)) {
                BookSupport.downImg(bookId, bookInfo.getCover());
            }

            List<CPVolume> volumeList = clientService.getVolumeList(partner, cpBookId);
            if (CollectionUtils.isEmpty(volumeList)) {
                throw new BookException(prefix + "章节列表为空");
            }

            boolean find = StringUtils.isBlank(bookId) || StringUtils.isBlank(lastChapterId);
            // 标记获取章节内容时是否出现异常，false为未出现异常
            boolean isContentException = false;

            for (Iterator<CPVolume> iterator = volumeList.iterator(); iterator.hasNext(); ) {
                CPVolume cpVolume = iterator.next();
                List<CPChapter> chapterList = cpVolume.getChapterList();

                if (CollectionUtils.isEmpty(chapterList)) {
                    logger.warn("{}[{}]章节列表为空", prefix, cpVolume.getName());
                    iterator.remove();
                    continue;
                }

                // 出现异常章节后，移除后续卷
                if (isContentException) {
                    iterator.remove();
                    continue;
                }

                for (Iterator<CPChapter> cpChapterIterator = chapterList.iterator(); cpChapterIterator.hasNext(); ) {
                    CPChapter cpChapter = cpChapterIterator.next();
                    String cpChapterId = cpChapter.getId();
                    String chapterName = cpChapter.getName();

                    if (!find) {
                        find = StringUtils.equals(lastChapterId, cpChapterId);
                        cpChapterIterator.remove();
                        continue;
                    }

                    // 出现异常章节后，从该章节开始，移除后续章节
                    if (isContentException) {
                        cpChapterIterator.remove();
                        continue;
                    }

                    // 文本文件才需要获取章节内容（当当除外，内容已经在章节列表中），南京萌鹿和蜻蜓FM的在章节列表中已经有内容地址，无需重复获取
                    boolean needGrabContent = (partner.getBookType() == BookTypeEnum.TXT.getType() && !ThirdPart.DANG_DANG.getCpId().equals(cpId)&& !ThirdPart.ZHU_JIE_DUAN_PIAN.getCpId().equals(cpId))
                            || (partner.getBookType() == BookTypeEnum.AUDIO.getType() && !ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId) && !ThirdPart.QING_TING_FM.getCpId().equals(cpId));

                    if (needGrabContent) {
                        CPChapter cpChapterInfo = null;

                        try {
                            cpChapterInfo = clientService.getCPChapterInfo(partner, cpBookId, cpVolume.getId(), cpChapterId);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            isContentException = true;
                            cpChapterIterator.remove();
                            TraceKeyHolder.setUserKey("_cpChapterId", cpChapterId);
                            TraceKeyHolder.setUserKey("_chapterName", chapterName);
                            exceptionBookRecordService.saveExceptionBookRecord(e.getMessage());
                            continue;
                        }

                        String content = cpChapterInfo.getContent();

                        // 章节内容小于20个字符也认为是异常章节
                        if (StringUtils.isNotBlank(content) && StringUtil.getRealLength(content) >= 20) {
                            cpChapter.setContent(content);
                        } else {
                            logger.error("{}[chapterId={}][chapterName={}]章节内容为空", prefix, cpChapterId, chapterName);
                            isContentException = true;
                            cpChapterIterator.remove();
                            exceptionBookRecordService.saveEmptyChapterContentRecord(bookId, cpChapterId, chapterName);
                            continue;
                        }

                        //有些cp是把章节名称放在bookinfo这个接口的
                        if (StringUtils.isBlank(chapterName)) {
                            cpChapter.setName(cpChapterInfo.getName());
                        }
                    }

                    // 章节名称为空，则认为是异常章节
                    if (StringUtils.isBlank(cpChapter.getName())) {
                        logger.error("{}[chapterId={}][chapterName={}]章节名称为空", prefix, cpChapterId, chapterName);
                        isContentException = true;
                        cpChapterIterator.remove();
                        continue;
                    }
                }

                if (chapterList.isEmpty()) {
                    iterator.remove();
                }
            }

            if (find) {
                if (CollectionUtils.isEmpty(volumeList)) {
                    logger.info(String.format("%s已同步到最新章节", prefix));

                    // 保存片花
                    audioTrailerService.saveTrailers(cpId, bookInfo.getAudioTrailers(), bookId);

                    //特殊cp检查内容更新
                    checkNeedUpdateContent(bookId, cpId, cpBookId);
                    // 没有出现异常的，删除异常记录表记录
                    if (!isContentException) {
                        exceptionBookRecordService.deleteExceptionBookRecord(bookId);
                    }

                    return;
                }

                bookInfo.setCpVolumeList(volumeList);
                syncService.sync(bookInfo);
            } else {
                TraceKeyHolder.setUserKey("_auto_cover", "1");
                syncService.cover(bookId);
            }
        } catch (BookException e) {
            String msg = e.getMessage();
            logger.error(msg, e);
            exceptionBookRecordService.saveExceptionBookRecord(msg);
            mailService.sendEmail(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            TraceKeyHolder.clear();
        }
    }

    private void checkNeedUpdateContent(String bookId, Long partnerId, String cpBookId) {
        if (StringUtils.isNotEmpty(bookId)) {
            shortStoryService.updateContentByPartner(bookId, cpBookId, partnerId);
        }
    }
}
