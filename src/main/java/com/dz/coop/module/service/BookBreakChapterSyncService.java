package com.dz.coop.module.service;

import com.dz.coop.module.model.BreakChapterConf;
import com.dz.vo.Ret;

import java.util.List;

/**
 * @project: coop-client
 * @description: 拆分书籍同步服务
 * @author: songwj
 * @date: 2020-01-10 21:02
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface BookBreakChapterSyncService {

    /**
     * 查询指定原始书籍的拆分书籍
     * @param bookId
     * @return
     */
    List<BreakChapterConf> getBreakChapterConfByBookId(String bookId);

    /**
     * 同步修改拆分书籍指定章节的内容
     * @param srcBookId 原始书籍id
     * @param srcChapterId 原始章节id
     * @param breakChapterConfs 拆分的书籍信息
     * @return
     */
    boolean modifyBreakChapterContent(String srcBookId, Long srcChapterId, List<BreakChapterConf> breakChapterConfs);

    /**
     * 覆盖按字数拆分书籍
     * @param newBookId
     * @return
     */
    Ret coverBreakBookByWordNum(String newBookId);

}
