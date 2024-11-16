package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 书籍更新发送消息类型
 * @author: songwj
 * @date: 2019-05-05 15:51
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public enum UpdateBookTypeEnum {

    /**
     * 书籍更新
     */
    BOOK_UPDATE(1),

    /**
     * 章节更新
     */
    CHAPTER_UPDATE(2),

    /**
     * 书籍章节同时更新
     */
    BOOK_AND_CHAPTER_UPDATE(3),

    /**
     * 书籍封面更新
     */
    BOOK_COVER_UPDATE(4),

    /**
     * 更新章节收费状态
     */
    CHAPTER_CHARGE_STATUS_UPDATE(5),

    /**
     * 章节修改更新
     */
    CHAPTER_MODIFY_UPDATE(6),

    /**
     * 章节删除更新
     */
    CHAPTER_DELETE_UPDATE(7),
    ;

    private Integer type;

    UpdateBookTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
