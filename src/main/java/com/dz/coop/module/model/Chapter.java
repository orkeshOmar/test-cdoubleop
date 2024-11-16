package com.dz.coop.module.model;


import com.dz.coop.common.annotation.Escape;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author panqz 2018-10-26 7:32 PM
 */

public class Chapter {
    private Long id;
    @NotNull
    private String cpChapterId;
    @NotNull
    private Long volumeId;
    @NotNull
    private String bookId;
    @Escape
    private String name;
    @NotNull
    private Integer isFree;

    private Integer isExist;
    private Integer wordNum;
    private Date ctime;
    private Date utime;

    private String cpVolumeId;

    @Escape
    private String content;

    private Integer isMore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpChapterId() {
        return cpChapterId;
    }

    public void setCpChapterId(String cpChapterId) {
        this.cpChapterId = cpChapterId;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }

    public Integer getIsExist() {
        return isExist;
    }

    public void setIsExist(Integer isExist) {
        this.isExist = isExist;
    }

    public Integer getWordNum() {
        return wordNum;
    }

    public void setWordNum(Integer wordNum) {
        this.wordNum = wordNum;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsMore() {
        return isMore;
    }

    public void setIsMore(Integer isMore) {
        this.isMore = isMore;
    }


    public String getCpVolumeId() {
        return cpVolumeId;
    }

    public void setCpVolumeId(String cpVolumeId) {
        this.cpVolumeId = cpVolumeId;
    }
}
