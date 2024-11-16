package com.dz.coop.module.model.owch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz 2018-10-30 9:49 AM
 */

public class OwchBook {
    private String bookId;
    private String cpBookId;
    private String partnerId;

    private String name;
    private String author;

    private String introduction;

    private String tag;


    private String cover;


    private String status;

    List<OwchVolume> volumes = new ArrayList<>();

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCpBookId() {
        return cpBookId;
    }

    public void setCpBookId(String cpBookId) {
        this.cpBookId = cpBookId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<OwchVolume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<OwchVolume> volumes) {
        this.volumes = volumes;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addVolumn(OwchVolume owchVolume) {
        this.volumes.add(owchVolume);
    }

}
