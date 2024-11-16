package com.dz.coop.module.model;

import java.util.Date;

/**
 * @author panqz 2018-12-10 6:29 PM
 */

public class ClassifyRel {
    private Integer id;
    private Integer cpId;
    private Integer classId;
    private Integer threeTypeId;
    private Integer twoTypeId;
    private Integer oneTypeId;

    private Integer baseTypeId;

    private Date ctime;
    private Date utime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCpId() {
        return cpId;
    }

    public void setCpId(Integer cpId) {
        this.cpId = cpId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getThreeTypeId() {
        return threeTypeId;
    }

    public void setThreeTypeId(Integer threeTypeId) {
        this.threeTypeId = threeTypeId;
    }

    public Integer getTwoTypeId() {
        return twoTypeId;
    }

    public void setTwoTypeId(Integer twoTypeId) {
        this.twoTypeId = twoTypeId;
    }

    public Integer getOneTypeId() {
        return oneTypeId;
    }

    public void setOneTypeId(Integer oneTypeId) {
        this.oneTypeId = oneTypeId;
    }

    public Integer getBaseTypeId() {
        return baseTypeId;
    }

    public void setBaseTypeId(Integer baseTypeId) {
        this.baseTypeId = baseTypeId;
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
