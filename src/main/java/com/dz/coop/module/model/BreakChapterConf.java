package com.dz.coop.module.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @project: coop-client
 * @description: 拆分章节配置对象
 * @author: songwj
 * @date: 2020-01-10 17:22
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class BreakChapterConf {

    private Long id;

    private String bookId;

    /**
     * 拆分的书籍id
     */
    private String newBookId;

    /**
     * 拆分的书名
     */
    private String newBookName;

    /**
     * 拆分章节数
     */
    private Integer breakChapterNum;

    /**
     * 开始拆分章节数
     */
    private Integer startChapter;

    /**
     * 最小章节字数
     */
    private Integer minChapterWords = 2000;

    /**
     * 0：失效，1：有效
     */
    private Integer status = 1;

    private Date ctime;

    private Date utime;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewBookId() {
        return newBookId;
    }

    public void setNewBookId(String newBookId) {
        this.newBookId = newBookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getNewBookName() {
        return newBookName;
    }

    public void setNewBookName(String newBookName) {
        this.newBookName = newBookName;
    }

    public Integer getBreakChapterNum() {
        return breakChapterNum;
    }

    public void setBreakChapterNum(Integer breakChapterNum) {
        this.breakChapterNum = breakChapterNum;
    }

    public Integer getStartChapter() {
        return startChapter;
    }

    public void setStartChapter(Integer startChapter) {
        this.startChapter = startChapter;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMinChapterWords() {
        return minChapterWords;
    }

    public void setMinChapterWords(Integer minChapterWords) {
        this.minChapterWords = minChapterWords;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
