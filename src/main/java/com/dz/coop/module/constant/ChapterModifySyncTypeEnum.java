package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 章节修改更新同步类型
 * @author: songwj
 * @date: 2020-03-02 0:16
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public enum ChapterModifySyncTypeEnum {

    /**
     * 全本同步修改
     */
    FULL(-1),

    /**
     * 最近半小时更新章节同步修改（默认）
     */
    RECENTLY(1),

    /**
     * 指定章节范围同步修改
     */
    RANGE(2),
    ;

    ChapterModifySyncTypeEnum(int type) {
        this.type = type;
    }

    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
