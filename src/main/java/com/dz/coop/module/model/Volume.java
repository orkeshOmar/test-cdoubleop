package com.dz.coop.module.model;

import com.dz.coop.common.annotation.Escape;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author panqz 2018-10-26 7:32 PM
 */

public class Volume {
    private Long id;
    @NotNull
    private String cpVolumeId;
    @NotNull
    @Escape
    private String name;
    @NotNull
    private String bookId;
    private Date ctime;
    private Date utime;

    private List<Chapter> chapters;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpVolumeId() {
        return cpVolumeId;
    }

    public void setCpVolumeId(String cpVolumeId) {
        this.cpVolumeId = cpVolumeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}
