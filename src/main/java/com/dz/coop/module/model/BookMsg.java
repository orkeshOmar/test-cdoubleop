package com.dz.coop.module.model;

/**
 * @author panqz 2018-10-27 12:00 PM
 */

public class BookMsg {

    private Partner partner;
    private String bookId;
    private String cpBookId;
    private String lastChapterId;
    private Integer lastChapterIndex;

    private String status;

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

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

    public String getLastChapterId() {
        return lastChapterId;
    }

    public void setLastChapterId(String lastChapterId) {
        this.lastChapterId = lastChapterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLastChapterIndex() {
        return lastChapterIndex;
    }

    public void setLastChapterIndex(Integer lastChapterIndex) {
        this.lastChapterIndex = lastChapterIndex;
    }
}
