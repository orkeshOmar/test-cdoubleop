package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 接口规范类型
 * @author: songwj
 * @date: 2019-07-25 19:21
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public enum InterfaceTypeEnum {
    /**
     * 我方标准
     */
    OUR_STANDARD(1),

    /**
     * 对方标准
     */
    OTHER_STANDARD(2),
    ;

    private int type;

    InterfaceTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
