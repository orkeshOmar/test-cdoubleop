package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 异常记录类型
 * @author: songwj
 * @date: 2020-03-10 19:28
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface ExceptionRecordType {

    /**
     * CP章节ID冲突
     */
    int KEY_CONFLICT = 1;

    /**
     * 章节内容为空
     */
    int CONTENT_IS_EMPTY = 2;

    /**
     * 书籍详情获取异常
     */
    int BOOK_DETAIL_EXCEPTION = 3;

    /**
     * 章节列表获取异常
     */
    int CHAPTER_LIST_EXCEPTION = 4;

    /**
     * 章节内容获取异常
     */
    int CHAPTER_CONTENT_EXCEPTION = 5;

}
