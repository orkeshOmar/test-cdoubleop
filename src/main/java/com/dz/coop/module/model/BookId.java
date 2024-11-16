package com.dz.coop.module.model;

import java.util.Date;

/**
 * @author panqz 2018-10-29 11:07 AM
 */

public class BookId {
    private Long id;
    private Date ctime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}
