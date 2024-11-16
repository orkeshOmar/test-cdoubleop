package com.dz.coop.module.service;

/**
 * @project: coop-client
 * @description: 异常书籍记录服务层
 * @author: songwj
 * @date: 2020-01-07 20:33
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface ExceptionBookRecordService {

    /**
     * 保存异常书籍记录
     * @param srcExceptionMsg 原始异常信息
     */
    boolean saveExceptionBookRecord(String srcExceptionMsg);

    /**
     * 保存章节内容为空记录
     * @param bookId
     * @param cpChapterId
     * @param chapterName
     * @return
     */
    boolean saveEmptyChapterContentRecord(String bookId, String cpChapterId, String chapterName);

    /**
     * 删除异常记录
     * @param bookId
     * @return
     */
    boolean deleteExceptionBookRecord(String bookId);

}
