package com.dz.coop.module.mapper;

import com.dz.coop.module.model.BreakChapterConf;
import com.dz.coop.module.model.cp.CPChapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @project: coop-client
 * @description: 拆分章节配置持久层
 * @author: songwj
 * @date: 2020-01-10 17:56
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface BreakChapterConfMapper {

    /**
     * 查询指定原始书籍的拆分书籍
     * @param bookId
     * @return
     */
    List<BreakChapterConf> getBreakChapterConfByBookId(String bookId);

    /**
     * 按字数拆分书籍查询拆分配置
     * @param newBookId
     * @return
     */
    BreakChapterConf getBreakChapterConfByNewBookId(String newBookId);

    /**
     * 获取原始书籍指定章节所拆分的书籍章节信息
     * @param bookId
     * @param chapterId
     * @return
     */
    List<CPChapter> getBreakNewChapterIds(@Param("bookId") String bookId, @Param("chapterId") Long chapterId);

    /**
     * 获取所有拆分书籍的原始书籍ID
     * @return
     */
    List<String> getAllBreakSourceBookIds();

}
