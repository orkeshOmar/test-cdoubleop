package com.dz.coop.module.service;


import com.dz.coop.module.model.cp.CPBook;

/**
 * @author panqz 2018-10-29 10:43 AM
 */

public interface SyncService {
    void sync(CPBook page);

    void cover(String bookId) throws Exception;

    /**
     * 章节覆盖
     * @param bookId
     * @param isforceCover 是否强制覆盖，true为是
     * @throws Exception
     */
    void cover(String bookId, boolean isforceCover) throws Exception;

    /**
     * 覆盖指定章节范围的内容
     * @param bookId 书籍id
     * @param startChapterId 从指定章节id开始，0表示从头开始
     * @param endChapterId 从指定章节id结束，-1表示到最后一章
     * @param isforceCover 是否强制覆盖，true为是
     * @throws Exception
     */
    void cover(String bookId, Long startChapterId, Long endChapterId, boolean isforceCover) throws Exception;
}
