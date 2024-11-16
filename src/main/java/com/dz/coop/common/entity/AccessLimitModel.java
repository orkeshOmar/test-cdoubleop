package com.dz.coop.common.entity;

import com.dz.coop.module.constant.ThirdPart;

/**
 * @project: coop-client
 * @description: 接口访问限制模型
 * @author: songwj
 * @date: 2020-11-07 18:02
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class AccessLimitModel {

    private ThirdPart thirdPart;

    /**
     * 限制次数
     */
    private int limitCount;

    /**
     * 限制时间（单位：秒）
     */
    private long limitTime;

    public AccessLimitModel() {}

    public AccessLimitModel(ThirdPart thirdPart, int limitCount, long limitTime) {
        this.thirdPart = thirdPart;
        this.limitCount = limitCount;
        this.limitTime = limitTime;
    }

    public ThirdPart getThirdPart() {
        return thirdPart;
    }

    public void setThirdPart(ThirdPart thirdPart) {
        this.thirdPart = thirdPart;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public long getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(long limitTime) {
        this.limitTime = limitTime;
    }

}
