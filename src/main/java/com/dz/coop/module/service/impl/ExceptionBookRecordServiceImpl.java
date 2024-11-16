package com.dz.coop.module.service.impl;

import com.alibaba.fastjson.JSON;
import com.dz.coop.module.constant.ExceptionRecordType;
import com.dz.coop.module.constant.KeyWordConstant;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.mapper.ExceptionBookRecordMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.vo.ExceptionBookRecordVO;
import com.dz.coop.module.service.ExceptionBookRecordService;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 异常书籍记录服务层
 * @author: songwj
 * @date: 2020-01-07 20:47
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class ExceptionBookRecordServiceImpl implements ExceptionBookRecordService {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionBookRecordServiceImpl.class);

    @Resource
    private ExceptionBookRecordMapper exceptionBookRecordMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private ChapterMapper chapterMapper;
    
    @Override
    public boolean saveExceptionBookRecord(String srcExceptionMsg) {
        ExceptionBookRecordVO exceptionBookRecordVO = null;

        try {
            // 不存放HttpHostConnectException、UnknownHostException异常
            if (StringUtils.isNotBlank(srcExceptionMsg) && !StringUtils.contains(srcExceptionMsg, KeyWordConstant.HTTP_HOST_CONNECT_EXCEPTION) && !StringUtils.contains(srcExceptionMsg, KeyWordConstant.UNKNOWN_HOST_EXCEPTION)) {
                String bookId = TraceKeyHolder.getUserKey("_bookId");

                if (StringUtils.isBlank(bookId)) {
                    return false;
                }

                Book book = bookMapper.getBookByBookId(bookId);

                if (book == null) {
                    return false;
                }

                String extend = null;
                if (!StringUtils.contains(srcExceptionMsg, KeyWordConstant.IDX_CPCHAPTERID_BOOKID)) {
                    Map<String, String> map = new HashMap<>(1);
                    int start = StringUtils.indexOf(srcExceptionMsg, KeyWordConstant.GET_EXCEPTION) + KeyWordConstant.GET_EXCEPTION.length() + 1;
                    map.put("remark", srcExceptionMsg.substring(start));
                    extend = JSON.toJSONString(map);
                }

                // 出现章节主键冲突
                if (StringUtils.contains(srcExceptionMsg, KeyWordConstant.IDX_CPCHAPTERID_BOOKID)) {
                    int start = StringUtils.indexOf(srcExceptionMsg, KeyWordConstant.IDX_CPCHAPTERID_BOOKID);
                    // CP章节ID-我方书籍，eg: 4724-11000000034
                    String duplicatKey = StringUtils.substring(srcExceptionMsg, start + KeyWordConstant.IDX_CPCHAPTERID_BOOKID.length());
                    String[] split = duplicatKey.split("-");
                    String cpChapterId = split[0];

                    Chapter chapter = chapterMapper.getChapterByCpChapterId(bookId, cpChapterId);

                    if (chapter != null) {
                        exceptionBookRecordVO = ExceptionBookRecordVO.build(book, chapter.getId(), chapter.getCpChapterId(), chapter.getName(), ExceptionRecordType.KEY_CONFLICT, null);
                    }
                    // 书籍详情获取异常
                } else if (StringUtils.contains(srcExceptionMsg, KeyWordConstant.BOOK_DETAIL_EXCEPTION)) {
                    exceptionBookRecordVO = ExceptionBookRecordVO.build(book, null, null, null, ExceptionRecordType.BOOK_DETAIL_EXCEPTION, extend);
                    // 章节列表获取异常
                } else if (StringUtils.contains(srcExceptionMsg, KeyWordConstant.CHAPTER_LIST_EXCEPTION)) {
                    exceptionBookRecordVO = ExceptionBookRecordVO.build(book, null, null, null, ExceptionRecordType.CHAPTER_LIST_EXCEPTION, extend);
                    // 章节内容获取异常
                } else if (StringUtils.contains(srcExceptionMsg, KeyWordConstant.CHAPTER_CONTENT_EXCEPTION)) {
                    String cpChapterId = TraceKeyHolder.getUserKey("_cpChapterId");
                    String chapterName = TraceKeyHolder.getUserKey("_chapterName");
                    exceptionBookRecordVO = ExceptionBookRecordVO.build(book, null, cpChapterId, chapterName, ExceptionRecordType.CHAPTER_CONTENT_EXCEPTION, extend);
                }

                if (exceptionBookRecordVO != null) {
                    exceptionBookRecordMapper.save(exceptionBookRecordVO);
                    logger.info("异常记录{}保存成功", exceptionBookRecordVO.toString());
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("抓取异常书籍记录存储失败：{}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean saveEmptyChapterContentRecord(String bookId, String cpChapterId, String chapterName) {
        ExceptionBookRecordVO exceptionBookRecordVO = null;

        try {
            if (StringUtils.isNotBlank(bookId)) {
                Book book = bookMapper.getBookByBookId(bookId);

                if (book != null) {
                    Map<String, String> map = new HashMap<>(1);
                    map.put("remark", TraceKeyHolder.getUserKey("url"));
                    exceptionBookRecordVO = ExceptionBookRecordVO.build(book, null, cpChapterId, chapterName, ExceptionRecordType.CONTENT_IS_EMPTY, JSON.toJSONString(map));
                }

                if (exceptionBookRecordVO != null) {
                    exceptionBookRecordMapper.save(exceptionBookRecordVO);
                    logger.info("异常记录{}保存成功", exceptionBookRecordVO.toString());
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("抓取空章节书籍记录存储失败：{}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteExceptionBookRecord(String bookId) {
        try {
            exceptionBookRecordMapper.deleteByBookId(bookId);
            return true;
        } catch (Exception e) {
            logger.error("[bookId={}]删除异常记录失败：{}", bookId, e.getMessage(), e);
            return false;
        }
    }

}
