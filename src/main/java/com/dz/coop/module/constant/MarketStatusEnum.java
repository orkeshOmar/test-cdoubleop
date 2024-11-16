package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: 书籍市场状态
 * @author: songwj
 * @date: 2019-11-27 11:53
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public enum MarketStatusEnum {

    /**
     * 上架
     */
    ON_SHELF(1),

    /**
     * 下架
     */
    UNSHELVE(2),

    /**
     * 入库
     */
    STORAGE(8),

    /**
     * 删除
     */
    DELETE(10),
    ;

    MarketStatusEnum(int type) {
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
