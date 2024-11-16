package com.dz.coop.module.model;

import java.util.Date;

/**
 * @Author: sunxm
 * @Description:
 * @Date: Created in 下午9:51 2018/1/23
 */
public class BookTypeThree {
    private Integer id;
    private Integer oneTypeId;
    private Integer twoTypeId;
    private String name;
    private String description;
    private Integer status;
    private Date ctime;
    private Date utime;

    private int cl_pay_ability;

    public int getCl_pay_ability() {
        return cl_pay_ability;
    }

    public void setCl_pay_ability(int cl_pay_ability) {
        this.cl_pay_ability = cl_pay_ability;
    }

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

    public Integer getTwoTypeId() {
        return twoTypeId;
    }

    public void setTwoTypeId(Integer twoTypeId) {
        this.twoTypeId = twoTypeId;
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
