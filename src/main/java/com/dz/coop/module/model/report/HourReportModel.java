package com.dz.coop.module.model.report;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @project: coop-client
 * @description: 小时报表模型
 * @author: songwj
 * @date: 2019-03-22 20:00
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class HourReportModel {

    /**
     * 小时时间
     */
    private String dtime;

    /**
     * 每小时更新数量
     */
    private Integer perHrUpdateNum;

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }

    public Integer getPerHrUpdateNum() {
        return perHrUpdateNum;
    }

    public void setPerHrUpdateNum(Integer perHrUpdateNum) {
        this.perHrUpdateNum = perHrUpdateNum;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
