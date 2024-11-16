package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.BookChapterVO;
import com.dz.coop.common.util.AliOssUtil;
import com.dz.coop.common.util.SendMQMsgUtil;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.module.constant.ChapterModifySyncTypeEnum;
import com.dz.coop.module.constant.UpdateBookTypeEnum;
import com.dz.coop.module.mapper.BreakBookContentMapper;
import com.dz.coop.module.mapper.BreakChapterConfMapper;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.model.BreakBookContent;
import com.dz.coop.module.model.BreakChapterConf;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.service.BookBreakChapterSyncService;
import com.dz.coop.module.service.ChapterService;
import com.dz.coop.module.support.ChapterSupport;
import com.dz.tools.JsonUtil;
import com.dz.vo.Ret;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @project: coop-client
 * @description: 拆分书籍同步服务
 * @author: songwj
 * @date: 2020-01-10 21:09
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BookBreakChapterSyncServiceImpl implements BookBreakChapterSyncService {

    private static final Logger logger = LoggerFactory.getLogger(BookBreakChapterSyncServiceImpl.class);

    /**
     * 按字数拆
     */
    private static final int BY_WORD_NUM = 2;

    private static final int ERROR_CODE = -1;

    @Resource
    private BreakChapterConfMapper breakChapterConfMapper;

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private ChapterService chapterService;

    @Resource
    private BreakBookContentMapper breakBookContentMapper;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Override
    public List<BreakChapterConf> getBreakChapterConfByBookId(String bookId) {
        return breakChapterConfMapper.getBreakChapterConfByBookId(bookId);
    }

    @Override
    public boolean modifyBreakChapterContent(String srcBookId, Long srcChapterId, List<BreakChapterConf> breakChapterConfs) {
        String prefix = String.format("bookId=%s,chapterId=%s", srcBookId, srcChapterId);

        try {
            logger.info("修改原始书籍{}内容触发同步修改到相应的拆分内容中！", prefix);
            List<CPChapter> breakNewChapterIds = new ArrayList<>();
            getAllBreakChapterIds(breakNewChapterIds, srcBookId, srcChapterId);
            if (CollectionUtils.isEmpty(breakNewChapterIds)) {
                logger.info("书籍没有对应的拆分章节对应关系！", prefix);
                return false;
            }

            Map<String, List<CPChapter>> chapterMap = new HashMap<>(breakNewChapterIds.size());
            for (CPChapter chapter : breakNewChapterIds) {
                String bookId = chapter.getBookId();
                if (chapterMap.containsKey(bookId)) {
                    chapterMap.get(bookId).add(chapter);
                } else {
                    chapterMap.put(bookId, new ArrayList<CPChapter>() {{
                        add(chapter);
                    }});
                }
            }

            for (BreakChapterConf breakChapterConf : breakChapterConfs) {
                // 按字数拆分的原始章节内容修改后不同步
                if (breakChapterConf.getType() == BY_WORD_NUM) {
                    logger.info("配置为按字数拆分类型，不同步修改章节内容，配置信息：{}", JsonUtil.obj2Json(breakChapterConf));
                    continue;
                }

                String newBookId = breakChapterConf.getNewBookId();
                List<CPChapter> cpChapters = chapterMap.get(newBookId);
                if (CollectionUtils.isEmpty(cpChapters)) {
                    logger.info("config-{}没有对应的拆分章节信息", JsonUtil.obj2Json(breakChapterConf));
                    continue;
                }
                int breakChapterNum = cpChapters.size();
                List<String> contentList = parseChapterContent(breakChapterNum, srcBookId, srcChapterId);

                for (int i = 0; i < breakChapterNum; i++) {
                    CPChapter chapter = cpChapters.get(i);

                    String name = chapter.getName();
                    String content = StringUtils.isNotBlank(name) ? name.concat("\r\n").concat(contentList.get(i)) : contentList.get(i);

                    if (StringUtils.isNotBlank(content)) {
                        logger.info("修改原始书籍章节{}同步更新了拆分书籍章节bookId={},chapterId={},chapterName={} 内容", prefix, chapter.getBookId(), chapter.getId(), chapter.getName());

                        BookChapterVO bookChapterVO = new BookChapterVO();

                        bookChapterVO.setId(Long.parseLong(chapter.getId()));
                        bookChapterVO.setContent(content);
                        bookChapterVO.setWordNum(StringUtil.getRealLength(content));
                        bookChapterVO.setIsExist(1);

                        adminBookRpc.updateBookChapter(bookChapterVO);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("{}同步修改拆分章节内容出现异常：{}", prefix, e.getMessage(), e);
        }

        return true;
    }

    /**
     * 递归获取所有原始修改章节所对应拆分章节
     * @param breakNewChapterIds
     * @param bookId
     * @param chapterId
     */
    private void getAllBreakChapterIds(List<CPChapter> breakNewChapterIds, String bookId, Long chapterId) {
        List<CPChapter> breakNewChapterIds1 = breakChapterConfMapper.getBreakNewChapterIds(bookId, chapterId);

        if (CollectionUtils.isNotEmpty(breakNewChapterIds1)) {
            breakNewChapterIds.addAll(breakNewChapterIds1);

            for (CPChapter chapter : breakNewChapterIds1) {
                getAllBreakChapterIds(breakNewChapterIds, chapter.getBookId(), Long.parseLong(chapter.getId()));
            }
        }
    }

    private List<String> parseChapterContent(int breakChapterNum, String bookId, Long chapterId) throws IOException {
        List<String> result = new ArrayList<>();
        String filePath = ChapterSupport.getChapterPath(bookId, chapterId);
        String content = AliOssUtil.getContent(filePath);
        List<String> removeEmptyLineList = new ArrayList<>();

        if (StringUtils.isNotBlank(content)) {
            String[] split = content.split("\n|\r\n");
            List<String> list = Arrays.asList(split);

            for (String row : list) {
                if (StringUtils.isNotBlank(row)) {
                    removeEmptyLineList.add(row);
                }
            }
        }

        if (removeEmptyLineList == null) {
            // 原章节内容为空时直接保存空章节避免后期出错
            result.add("");
            return result;
        }

        int size = (int) Math.floor(removeEmptyLineList.size() / breakChapterNum);
        int sizeCur = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < removeEmptyLineList.size(); i++) {
            sb.append(removeEmptyLineList.get(i)).append("\r\n");
            sizeCur++;
            boolean flag = (size == sizeCur && result.size() < (breakChapterNum - 1)) || i == (removeEmptyLineList.size() - 1);

            if (flag) {
                result.add(sb.toString());
                sb.setLength(0);
                sizeCur = 0;
            }
        }

        return result;
    }

    @Override
    public Ret coverBreakBookByWordNum(String newBookId) {
        try {
            BreakChapterConf breakChapterConf = breakChapterConfMapper.getBreakChapterConfByNewBookId(newBookId);

            if (breakChapterConf == null) {
                return Ret.error(ERROR_CODE, "未查询到该拆分书籍信息");
            }

            if (breakChapterConf.getType().intValue() != BY_WORD_NUM) {
                return Ret.error(ERROR_CODE, "该书籍不是按字数拆分书籍，不支持覆盖");
            }

            List<BookChapterVO> bookChapterVOS = chapterService.getAllChapterByBookId(newBookId);

            if (CollectionUtils.isEmpty(bookChapterVOS)) {
                return Ret.error(ERROR_CODE, "该书籍没有对应拆分的章节");
            }

            // 原始书籍ID
            String bookId = breakChapterConf.getBookId();
            List<BookChapterVO> srcBookChapters = chapterService.getAllChapterByBookId(bookId);
            String remainContent = "";
            List<String> allContentList = new ArrayList<>();

            // 将所有原始章节内容按字数拆分
            for (int n = 0, len = srcBookChapters.size(); n < len; n++) {
                BookChapterVO bookChapterVO = srcBookChapters.get(n);
                Long chapterId = bookChapterVO.getId();
                BookChapterVO chapterVO = chapterService.getBookChapter(chapterId);

                String srcContent = StringUtils.isNotBlank(remainContent) ? remainContent + "\r\n" + removeTitle(chapterVO.getContent()) : removeTitle(chapterVO.getContent());
                List<String> contentList = new ArrayList<>();

                // 从指定章节开始拆
                if ((n + 1) >= breakChapterConf.getStartChapter()) {
                    contentList = praseChapterContentByWordCount(srcContent, 1000);
                    logger.info("bookId={},chapterId={},chapterName={},newBookId={},拆分章节数量为：{}", bookId, chapterId, chapterVO.getName(), newBookId, contentList.size());

                    // 如果原始书不为最后一章，则拆出的章节的最后一章不足1000字，放到下一章来拼接
                    if (n != len - 1) {
                        String lastContent = contentList.get(contentList.size() - 1);
                        if (StringUtil.getRealLength(lastContent) < 1000) {
                            remainContent = lastContent;
                            contentList.remove(contentList.size() - 1);
                        }
                    }
                } else {
                    contentList.add(srcContent);
                }

                allContentList.addAll(contentList);
            }

            // 原拆分书籍章节数
            int breakBookTotalChapterNum = bookChapterVOS.size();
            // 现拆分书籍章节
            int currentBreakChapterNum = allContentList.size();
            logger.info("bookId={},拆分后的章节数={},newBookId={},原始拆分章节数={}", bookId, currentBreakChapterNum, newBookId, breakBookTotalChapterNum);

            // 如果当前拆分书籍章节数大于原拆分书籍章节数，则正常覆盖，多出的章节存入未拆完章节内容表
            if (currentBreakChapterNum > breakBookTotalChapterNum) {
                boolean ret = coverChapter(bookChapterVOS, allContentList, newBookId, bookId, breakBookTotalChapterNum);

                if (!ret) {
                    return Ret.error(ERROR_CODE, "书籍章节覆盖失败");
                }

                // 提取出的部分章节内容，存入数据库
                StringBuilder extraContent = new StringBuilder("");
                for (int m = breakBookTotalChapterNum; m < currentBreakChapterNum; m++) {
                    extraContent.append(allContentList.get(m)).append("\r\n");
                }

                BreakBookContent breakBookContent = new BreakBookContent();
                breakBookContent.setBookId(newBookId);
                breakBookContent.setContent(extraContent.toString());
                breakBookContentMapper.insertOrUpdate(breakBookContent);
                logger.info("newBookId={},bookId={},覆盖后多余章节内容入库成功", newBookId, bookId);
                // 如果当前拆分书籍章节数小于等于原拆分书籍章节数，则正常覆盖后，删除数据库中未拆完章节内容表记录
            } else {
                boolean ret = coverChapter(bookChapterVOS, allContentList, newBookId, bookId, currentBreakChapterNum);

                if (!ret) {
                    return Ret.error(ERROR_CODE, "书籍章节覆盖失败");
                }

                breakBookContentMapper.deleteByBookId(newBookId);
                logger.info("newBookId={},bookId={},覆盖后删除数据库中多余章节内容", newBookId, bookId);
            }

            SendMQMsgUtil.send(newBookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType(), ChapterModifySyncTypeEnum.RANGE.getType(), 0L, -1L);

            return Ret.success("书籍覆盖成功");
        } catch (Exception e) {
            logger.error("覆盖按字数拆分书籍失败：{}", e.getMessage(), e);
            return Ret.error(ERROR_CODE, "书籍覆盖失败");
        }
    }

    private boolean coverChapter(List<BookChapterVO> bookChapterVOS, List<String> allContentList, String newBookId, String bookId, int chapterNum) {
        try {
            for (int n = 0; n < chapterNum; n++) {
                BookChapterVO chapterVO = bookChapterVOS.get(n);
                Long chapterId = chapterVO.getId();
                String chapterName = chapterVO.getName();
                String content = chapterName.concat("\r\n").concat(allContentList.get(n));

                BookChapterVO bookChapterVO = new BookChapterVO();
                bookChapterVO.setId(chapterId);
                bookChapterVO.setBookId(newBookId);
                bookChapterVO.setName(chapterName);
                bookChapterVO.setContent(content);
                bookChapterVO.setWordNum(StringUtil.getRealLength(content));
                bookChapterVO.setIsExist(1);
                Integer ret = chapterService.updateBookChapter(bookChapterVO);

                if (ret.intValue() == 1) {
                    logger.info("newBookId={},chapterId={},chapterName={},bookId={}, 章节覆盖成功", newBookId, chapterId, chapterName, bookId);
                } else {
                    logger.error("newBookId={},chapterId={},chapterName={},bookId={}, 章节覆盖失败", newBookId, chapterId, chapterName, bookId);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("newBookId={},bookId={},书籍覆盖失败：{}", newBookId, bookId, e.getMessage(), e);
            return false;
        }
    }

    private static String removeTitle(String content) {
        if (StringUtils.isNotBlank(content.trim())) {
            String[] split = content.split("\n|\r\n");
            List<String> list = new ArrayList<>(Arrays.asList(split));
            list.remove(0);
            return String.join("\r\n", list);
        }
        return "";
    }

    private static List<String> praseChapterContentByWordCount(String content, int wordCount) {
        List<String> result = new ArrayList<>();
        List<String> removeEmptyLineList = new ArrayList<>();

        if (StringUtils.isNotBlank(content.trim())) {
            String[] split = content.split("\n|\r\n");
            List<String> list = Arrays.asList(split);

            for (String row : list) {
                if (StringUtils.isNotBlank(row)) {
                    removeEmptyLineList.add(row);
                }
            }
        }

        if (CollectionUtils.isEmpty(removeEmptyLineList)) {
            // 原章节内容为空时 直接保存空章节避免后期出错
            result.add("");
            return result;
        }

        StringBuilder con = new StringBuilder();
        removeEmptyLineList.forEach(c -> {
            con.append(c).append("\r\n");
            if (StringUtil.getRealLength(con.toString()) > wordCount) {
                result.add(con.toString());
                con.delete(0, con.length());
            }
        });

        if (con.length() > 0) {
            result.add(con.toString());
        }

        return result;
    }

}
