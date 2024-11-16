package com.dz.coop.module.model;

import java.util.Date;

/**
 * @author panqz 2018-10-27 6:29 PM
 */

public class PartnerUrl {
    private Long id;
    private Long pid;
    private String bookListUrl;
    private String bookInfoUrl;
    private String chapterListUrl;
    private String chapterInfoUrl;
    private String categoryListUrl;
    private Date ctime;
    private Date utime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getBookListUrl() {
        return bookListUrl;
    }

    public void setBookListUrl(String bookListUrl) {
        this.bookListUrl = bookListUrl;
    }

    public String getBookInfoUrl() {
        return bookInfoUrl;
    }

    public void setBookInfoUrl(String bookInfoUrl) {
        this.bookInfoUrl = bookInfoUrl;
    }

    public String getChapterListUrl() {
        return chapterListUrl;
    }

    public void setChapterListUrl(String chapterListUrl) {
        this.chapterListUrl = chapterListUrl;
    }

    public String getChapterInfoUrl() {
        return chapterInfoUrl;
    }

    public void setChapterInfoUrl(String chapterInfoUrl) {
        this.chapterInfoUrl = chapterInfoUrl;
    }


    public String getCategoryListUrl() {
        return categoryListUrl;
    }

    public void setCategoryListUrl(String categoryListUrl) {
        this.categoryListUrl = categoryListUrl;
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
}
