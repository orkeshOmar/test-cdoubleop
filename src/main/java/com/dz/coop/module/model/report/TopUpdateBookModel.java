package com.dz.coop.module.model.report;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @project: coop-client
 * @description: Top书籍各个cp统计模型
 * @author: songwj
 * @date: 2019-03-22 20:05
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class TopUpdateBookModel {

    /**
     * cpID
     */
    private Long partnerId;

    /**
     * cp名称
     */
    private String partnerName;

    /**
     * 更新书籍数量
     */
    private Integer updateBookNum;

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getUpdateBookNum() {
        return updateBookNum;
    }

    public void setUpdateBookNum(Integer updateBookNum) {
        this.updateBookNum = updateBookNum;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
