package com.dz.coop.module.model;

import java.util.List;

/**
 * Created by shanjianyu on 2018/1/23.
 */
public class BookTypeTwo {
    private Integer id;
    private Integer oneTypeId;
    private String name;
    private String description;
    private String img;
    private String cornerName;
    private String cornerColor;
    private Integer status;
    private Integer sort;
    private String ctime;
    private String utime;


    private List<BookTypeThree> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOneTypeId() {
        return oneTypeId;
    }

    public void setOneTypeId(Integer oneTypeId) {
        this.oneTypeId = oneTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCornerName() {
        return cornerName;
    }

    public void setCornerName(String cornerName) {
        this.cornerName = cornerName;
    }

    public String getCornerColor() {
        return cornerColor;
    }

    public void setCornerColor(String cornerColor) {
        this.cornerColor = cornerColor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public List<BookTypeThree> getChildren() {
        return children;
    }

    public void setChildren(List<BookTypeThree> children) {
        this.children = children;
    }

}
