package com.dz.coop.module.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * @project: coop-client
 * @description: 音频片花
 * @author: songwj
 * @date: 2020-12-02 21:39
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class AudioTrailer implements Serializable {

    /**
     * 书籍ID
     */
    private String bookId;

    /**
     * 片花ID
     */
    private Long trailerId;

    /**
     * 片花简介
     */
    private String brief;

    /**
     * 播放地址
     */
    private String url;

    /**
     * 最后更新时间
     */
    private Date lastUtime;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Long getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(Long trailerId) {
        this.trailerId = trailerId;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastUtime() {
        return lastUtime;
    }

    public void setLastUtime(Date lastUtime) {
        this.lastUtime = lastUtime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
