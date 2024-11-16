package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.BookChapterQueryRequest;
import com.dz.content.api.book.vo.BookChapterVO;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.*;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.KeyWordConstant;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.vo.SaveChapterResp;
import com.dz.coop.module.service.ChapterService;
import com.dz.coop.module.support.BookSupport;
import com.dz.coop.module.support.ChapterSupport;
import com.dz.glory.common.vo.Page;
import com.dz.glory.common.vo.Ret;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author panqz 2018-12-11 11:43 PM
 */
@Service
public class ChapterServiceImpl implements ChapterService {
    private static final Logger logger = LoggerFactory.getLogger(ChapterServiceImpl.class);

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private ChapterService chapterService;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Override
    public SaveChapterResp saveChapter(CPChapter chapter, String bookId, Long volumeId, boolean isInnerDz, int bookType, Long cpId) {
        SaveChapterResp resp = new SaveChapterResp();
        CheckUtil.check(chapter);

        int count = chapterService.countChapters(bookId);

        Chapter owchChapter = new Chapter();
        owchChapter.setName(chapter.getName());
        String content = chapter.getContent();
        owchChapter.setBookId(bookId);
        owchChapter.setCpChapterId(chapter.getId());
        //点众使用自己的章节计费方式
        Integer isFree = chapter.getIsFree();
        boolean isQingTing = cpId == ThirdPart.QING_TING_FM.getCpId().longValue();
        boolean isMuDanYuanDuanPian = cpId == ThirdPart.MU_DAN_YUAN_DUAN_PIAN.getCpId().longValue();
        boolean isHuaYuanLuDuanPian = cpId == ThirdPart.HUA_YUAN_LU_DUAN_PIAN.getCpId().longValue();
        boolean enforce3pRequire = isQingTing || isMuDanYuanDuanPian || isHuaYuanLuDuanPian;
        owchChapter.setIsFree((enforce3pRequire || (isInnerDz && isFree != null) ? isFree : ++count > 20 ? 1 : 0));
        owchChapter.setVolumeId(volumeId);

        EscapeUtil.escape(owchChapter);

        BookChapterVO bookChapterVO = BeanUtil.convertFrom(owchChapter, BookChapterVO.class);

        Ret<BookChapterVO> bookChapterVORet = adminBookRpc.addBookChapter(bookChapterVO);

        if (bookChapterVORet.getStatus() == 600) {
            throw new BookException("{}{}", KeyWordConstant.IDX_CPCHAPTERID_BOOKID, bookChapterVORet.getMessage());
        }

        BookChapterVO chapterVO = bookChapterVORet.getData();

        Long chapterId = chapterVO.getId();

        boolean isAudio = bookType == BookTypeEnum.AUDIO.getType();
        String audioTempPath = null;
        String ossFilePath = null;

        try {
            if (!isAudio) {
                content = dealContent(chapter.getContent(), isInnerDz, chapter.getName());
                ossFilePath = ChapterSupport.getChapterPath(bookId, chapterId);
                chapterVO.setContent(content);
                resp.setSize(content.length());

                if (StringUtils.isNotBlank(content) && StringUtil.containsHtml(content)) {
                    logger.error("书籍章节内容疑似包含HTML标签,bookId={},cpChapterId={},chapterName={}", bookId, chapter.getId(), chapter.getName());
                }
            } else {
                // 蜻蜓FM不需要下载音频文件
                if (!isQingTing) {
                    // 有声中企瑞铭不下载音频文件
                    if (!ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(cpId)) {
                        String format = getChapterPathSuffix(chapter, cpId);
                        ossFilePath = ChapterSupport.getAudioChapterPath(bookId, chapterId, format);
                        audioTempPath = BookSupport.getAudioLocalPath(ossFilePath);
                        boolean res = AudioUtil.download(content, audioTempPath);

                        if (res == false) {
                            throw new BookException("bookId={},chapterId={},chapterName={} audio file download failed", bookId, chapter.getId(), chapter.getName());
                        }

                        // 上传音频到OSS
                        AliAudioOssUtil.setFile(ossFilePath, new FileInputStream(audioTempPath));
                        FileUtils.deleteQuietly(new File(audioTempPath));
                        logger.info("音频临时文件[{}]删除成功", audioTempPath);
                    }

                    if (ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId)) {
                        chapterVO.setSuffix(ChapterSupport.M4A);
                        // 单位kb
                        Long size = new File(audioTempPath).exists() ? new File(audioTempPath).length() / 1024 : -1;
                        chapterVO.setSize(size.intValue());
                    } else {
                        chapterVO.setSuffix(chapter.getFormat());
                        chapterVO.setSize(chapter.getSize());
                        chapterVO.setUrl(dealAudioContent(bookId, chapterId, chapter, cpId));
                    }
                }
            }
        } catch (Exception e) {
            adminBookRpc.deleteBookChapter(chapterVO);

            if (isAudio) {
                AliAudioOssUtil.deleteFile(ossFilePath);
            } else {
                AliOssUtil.deleteFile(ossFilePath);
            }

            throw new BookException("bookId={},chapterId={},chapterName={} content save fail", bookId, chapter.getId(), chapter.getName());
        }

        // 当为有声读物时，wordNum存放音频时长
        int contentLen = !isAudio ? StringUtil.getRealLength(content) : (ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId) ? (int) AudioUtil.getDuration(audioTempPath) : chapter.getDuration());

        chapterVO.setIsExist(1);
        chapterVO.setWordNum(contentLen);
        Integer ret = adminBookRpc.updateBookChapter(chapterVO).getData();

        if (ret == 0) {
            adminBookRpc.deleteBookChapter(chapterVO);

            if (isAudio) {
                AliAudioOssUtil.deleteFile(ossFilePath);
            } else {
                AliOssUtil.deleteFile(ossFilePath);
            }

            throw new BookException("bookId={},chapterId={},chapterName={} content save fail", bookId, chapter.getId(), chapter.getName());
        }

        logger.info("bookId={},chapterId={},chapterName={} 章节内容保存成功", bookId, chapter.getId(), chapter.getName());
        resp.setChapterId(chapter.getId());
        return resp;
    }

    @Override
    public SaveChapterResp updateChapter(String bookId, CPChapter cpChapter, Chapter owchChapter, boolean isInnerDz, Partner partner) {
        SaveChapterResp resp = new SaveChapterResp();
        String content = partner.getBookType() == BookTypeEnum.AUDIO.getType() ? cpChapter.getContent() : dealContent(cpChapter.getContent(), isInnerDz, cpChapter.getName());

        Long chapterId = owchChapter.getId();
        resp.setChapterId(chapterId.toString());

        try {
            Long cpId = partner.getId();
            int contentLen;
            BookChapterVO bookChapterVO = BeanUtil.convertFrom(owchChapter, BookChapterVO.class);

            if (partner.getBookType() == BookTypeEnum.AUDIO.getType()) {
                if (ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(cpId)) {
                    contentLen = cpChapter.getDuration();
                    chapterService.updateChapterExtend(bookId, chapterId.toString(), cpChapter, cpId, bookChapterVO);
                } else {
                    String suffixFormat = getChapterPathSuffix(cpChapter, cpId);
                    String audioChapterPath = ChapterSupport.getAudioChapterPath(bookId, chapterId, suffixFormat);
                    String localAudioPath = BookSupport.getAudioLocalPath(audioChapterPath);
                    boolean res = AudioUtil.download(content, localAudioPath);

                    if (res == false) {
                        throw new BookException("bookId={},chapterId={},chapterName={} audio file download failed", bookId, chapterId.toString(), cpChapter.getName());
                    }

                    // 上传音频到OSS
                    AliAudioOssUtil.setFile(audioChapterPath, new FileInputStream(localAudioPath));

                    FileUtils.deleteQuietly(new File(localAudioPath));
                    logger.info("音频临时文件[{}]删除成功", localAudioPath);

                    contentLen = ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId) ? (int) AudioUtil.getDuration(localAudioPath) : cpChapter.getDuration();
                    // 更新章节扩展表中的音频格式信息
                    if (ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId)) {
                        chapterService.updateChapterExtend(bookId, chapterId.toString(), suffixFormat, bookChapterVO);
                    } else {
                        chapterService.updateChapterExtend(bookId, chapterId.toString(), cpChapter, cpId, bookChapterVO);
                    }
                }
            } else {
                bookChapterVO.setContent(content);
                contentLen = StringUtil.getRealLength(content);
            }

            bookChapterVO.setName(EscapeUtil.escape(cpChapter.getName()));
            bookChapterVO.setCpChapterId(cpChapter.getId());
            bookChapterVO.setWordNum(contentLen);
            bookChapterVO.setIsExist(1);
            bookChapterVO.setIsMore(0);
            resp.setSize(contentLen);

            adminBookRpc.updateBookChapter(bookChapterVO);

            logger.info("bookid={},chapterid={} 章节内容覆盖成功", bookId, owchChapter.getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resp;
    }


    private String dealContent(String content, boolean isInnerDz, String chapterName) {
        if (!isInnerDz) {
            content = chapterName + "\n" + content;
        }
        content = EscapeUtil.escape(content);
        return content;
    }

    private String dealAudioContent(String bookId, Long chapterId, CPChapter chapter, Long cpId) {
        String content = chapter.getContent();
        return ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(cpId) ? content.substring(content.indexOf("/", 9)) : "/" + ChapterSupport.getAudioChapterPath(bookId, chapterId, "." + chapter.getFormat());
    }

    @Override
    public void updateChapterExtend(String bookId, String chapterId, String audioFormat, BookChapterVO bookChapterVO) {
        try {
            String audioPath = ChapterSupport.getAudioChapterPath(bookId, Long.parseLong(chapterId), audioFormat);
            String localAudioPath = BookSupport.getAudioLocalPath(audioPath);
            Long size = new File(localAudioPath).exists() ? new File(localAudioPath).length() / 1024 : -1;
            bookChapterVO.setSuffix(audioFormat.replaceAll("\\.", ""));
            bookChapterVO.setSize(size.intValue());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateChapterExtend(String bookId, String chapterId, CPChapter cpChapter, Long cpId, BookChapterVO bookChapterVO) {
        try {
            bookChapterVO.setSuffix(cpChapter.getFormat());
            bookChapterVO.setSize(cpChapter.getSize());
            bookChapterVO.setUrl(dealAudioContent(bookId, Long.valueOf(chapterId), cpChapter, cpId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String getChapterPathSuffix(CPChapter cpChapter, Long cpId) {
        return ThirdPart.NAN_JING_MENG_LU.getCpId().equals(cpId) ? ChapterSupport.AUDIO_CHAPTER_SUFFIX : "." + cpChapter.getFormat();
    }

    @Override
    public Chapter getLastChapter(String bookId) {
        return chapterMapper.getLastChapter(bookId);
    }

    @Override
    public Integer countChapters(String bookId) {
        BookChapterQueryRequest request = new BookChapterQueryRequest();
        request.setBookId(bookId);
        request.setLimit(1);
        Page<BookChapterVO> bookChapterVOPage = adminBookRpc.getListBookChapter(request).getData();
        return (int) bookChapterVOPage.getTotalNum();
    }

    @Override
    public Map<String, Object> countBookWordNumAndChapterNum(String bookId) {
        return chapterMapper.countBookWordNumAndChapterNum(bookId);
    }

    @Override
    public List<Chapter> listMoreChapter(String bookId) {
        return chapterMapper.listMoreChapter(bookId);
    }

    @Override
    public List<BookChapterVO> getAllChapterByBookId(String bookId) {
        BookChapterQueryRequest request = new BookChapterQueryRequest();
        request.setBookId(bookId);
        request.addOrderBy("id", true);
        return adminBookRpc.getAllBookChapter(request).getData();
    }

    @Override
    public BookChapterVO getBookChapter(Long chapterId) {
        return adminBookRpc.getBookChapter(chapterId).getData();
    }

    @Override
    public Integer updateBookChapter(BookChapterVO bookChapterVO) {
        return adminBookRpc.updateBookChapter(bookChapterVO).getData();
    }

}
