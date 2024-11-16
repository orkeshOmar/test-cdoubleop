package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 书籍类型
 * @author: songwj
 * @date: 2019-07-25 18:08
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public enum BookTypeEnum {
    TXT(1, "文本"),
    AUDIO(2, "有声读物"),
    ;

    private int type;

    private String desc;

    BookTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
