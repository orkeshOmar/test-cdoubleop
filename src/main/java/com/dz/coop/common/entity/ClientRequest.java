package com.dz.coop.common.entity;


import com.dz.coop.module.model.Partner;

/**
 * @author panqz 2018-10-27 11:21 AM
 */

public class ClientRequest {
    private Partner partner;
    private String bookId;
    private String volumeId;
    private String chapterId;

    public ClientRequest() {
    }

    public ClientRequest(Builder builder) {
        this.partner = builder.partner;
        this.bookId = builder.bookId;
        this.chapterId = builder.chapterId;
        this.volumeId = builder.volumeId;
    }

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

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public static Builder create(Partner partner) {
        return new Builder(partner);
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    static class Builder {
        private final Partner partner;
        private String bookId;
        private String volumeId;
        private String chapterId;

        public Builder(Partner partner) {
            this.partner = partner;
        }

        public Builder bookId(String bookId) {
            this.bookId = bookId;
            return this;
        }

        public Builder chapterId(String chapterId) {
            this.chapterId = chapterId;
            return this;
        }

        public Builder volumeID(String volumeId) {
            this.volumeId = volumeId;
            return this;
        }

        public ClientRequest build() {
            return new ClientRequest(this);
        }
    }
}
