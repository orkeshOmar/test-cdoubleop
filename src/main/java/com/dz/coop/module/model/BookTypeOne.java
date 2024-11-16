package com.dz.coop.module.model;

import java.util.List;

/**
 * Created by shanjianyu on 2018/1/23.
 */
public class BookTypeOne {
    private Integer id;
    private String name;
    private String description;
    private String img;
    private String isShow;
    private Integer sort;
    private String ctime;
    private String utime;



    private List<BookTypeTwo> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
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

    public List<BookTypeTwo> getChildren() {
        return children;
    }

    public void setChildren(List<BookTypeTwo> children) {
        this.children = children;
    }


}

