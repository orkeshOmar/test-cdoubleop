package com.dz.coop.module.model.report;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @project: coop-client
 * @description: 接口新书籍模型
 * @author: songwj
 * @date: 2019-04-13 16:10
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class NewBookModel {

    private Long cpPartnerId;

    private String cpName;

    private String cpBookId;

    private String cpBookName;

    private String bookId;

    public Long getCpPartnerId() {
        return cpPartnerId;
    }

    public void setCpPartnerId(Long cpPartnerId) {
        this.cpPartnerId = cpPartnerId;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getCpBookId() {
        return cpBookId;
    }

    public void setCpBookId(String cpBookId) {
        this.cpBookId = cpBookId;
    }

    public String getCpBookName() {
        return cpBookName;
    }

    public void setCpBookName(String cpBookName) {
        this.cpBookName = cpBookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
