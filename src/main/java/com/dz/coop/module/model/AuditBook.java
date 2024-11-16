package com.dz.coop.module.model;

/**
 * @author panqz 2018-12-17 11:51 PM
 */

public class AuditBook {
    private Integer id;
    private String bookId;
    private String bookName;

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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
