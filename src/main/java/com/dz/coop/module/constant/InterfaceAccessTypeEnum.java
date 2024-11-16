package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 接口访问类型枚举
 * @author: songwj
 * @date: 2019-11-15 11:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public enum InterfaceAccessTypeEnum {

    /**
     * 书籍列表
     */
    BOOK_LIST("bookList"),

    /**
     * 书籍详情
     */
    BOOK_INFO("bookInfo"),

    /**
     * 章节列表
     */
    CHAPTER_LIST("chapterList"),

    /**
     * 章节内容
     */
    CHAPTER_INFO("chapterInfo"),
    ;

    InterfaceAccessTypeEnum(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
