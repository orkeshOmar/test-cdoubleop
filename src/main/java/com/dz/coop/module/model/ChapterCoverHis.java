package com.dz.coop.module.model;

import java.util.Date;

/**
 * @author panqz 2018-12-16 11:34 PM
 */

public class ChapterCoverHis {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    private Integer id;
    private String bookId;
    private String lasterChapterId;
    private Integer status;
    private Date ctime;
    private Date utime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getLasterChapterId() {
        return lasterChapterId;
    }

    public void setLasterChapterId(String lasterChapterId) {
        this.lasterChapterId = lasterChapterId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
