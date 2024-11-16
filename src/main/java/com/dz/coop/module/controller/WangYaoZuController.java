package com.dz.coop.module.controller;

import com.dz.coop.common.util.AliOssUtil;
import com.dz.coop.common.util.SendMQMsgUtil;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.module.constant.UpdateBookTypeEnum;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.mapper.UpdateMsgqueueMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.UpdateMsg;
import com.dz.coop.module.support.ChapterSupport;
import com.dz.vo.Ret;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @project: coop-client
 * @description: 王瑶组书籍处理
 * @author: songwj
 * @date: 2019-11-04 17:43
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RestController
@RequestMapping("/portal/wangYaoZu")
public class WangYaoZuController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UpdateMsgqueueMapper updateMsgqueueMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private ChapterMapper chapterMapper;

    /**
     * 检查并添加指定书籍对应章节标题
     * @param bookId
     * @return
     */
    @RequestMapping(value = "/addChapterTitle/{bookId}")
    @ResponseBody
    public Ret addChapterTitle(@PathVariable("bookId") String bookId) {
        try {
            updateChapterTitle(bookId);
            return Ret.success("书籍bookId=[" + bookId + "]章节信息更新成功");
        } catch (Exception e) {
            logger.error("书籍bookId={}章节信息更新失败!!!", bookId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]章节信息更新失败");
        }
    }

    /**
     * 检查并添加指定cp所有书籍对应章节标题
     * @param partnerId
     * @return
     */
    @RequestMapping(value = "/addChapterTitleByPartner/{partnerId}")
    @ResponseBody
    public Ret addChapterTitleByPartner(@PathVariable("partnerId") Long partnerId) {
        try {
            List<String> bookIds = bookMapper.getAllBookIdByPartner(partnerId);

            if (CollectionUtils.isNotEmpty(bookIds)) {
                for (String bookId : bookIds) {
                    updateChapterTitle(bookId);
                }
            }

            return Ret.success("partnerId=[" + partnerId + "]章节信息更新成功");
        } catch (Exception e) {
            logger.error("partnerId={}章节信息更新失败!!!", partnerId, e);
            return Ret.error(-1, "partnerId=[" + partnerId + "]章节信息更新失败");
        }
    }

    private void updateChapterTitle(String bookId) {
        try {
            List<Chapter> chapters = chapterMapper.listChapters(bookId);
            boolean needUpdate = false;

            if (CollectionUtils.isNotEmpty(chapters)) {
                for (Chapter chapter : chapters) {
                    String chapterPath = ChapterSupport.getChapterPath(bookId, chapter.getId());
                    File file = new File(chapterPath);

                    if (AliOssUtil.doesObjectExist(chapterPath)) {
                        // 章节标题
                        String name = chapter.getName();
                        // 章节内容
                        String content = AliOssUtil.getContent(chapterPath);

                        // 如果章节内容未添加标题，则添加标题
                        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(name) && !content.startsWith(name)) {
                            content = name + "\n" + content;
                            AliOssUtil.setContent(chapterPath, content);
                            Chapter chapterInfo = new Chapter();
                            chapterInfo.setId(chapter.getId());
                            chapterInfo.setWordNum(StringUtil.getRealLength(content));
                            chapterInfo.setIsExist(1);
                            chapterMapper.updateChapterInfo(chapterInfo);
                            logger.info("bookId={},chapterId={},cpChapterId={},chapterName={} 章节内容标题添加成功", bookId, chapter.getId(), chapter.getCpChapterId(), name);
                            needUpdate = true;
                        }
                    }
                }
            }

            if (needUpdate) {
                SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType());
            }
        } catch (Exception e) {
            logger.error("书籍bookId={}章节信息更新失败!!!", bookId, e);
        }
    }

}
