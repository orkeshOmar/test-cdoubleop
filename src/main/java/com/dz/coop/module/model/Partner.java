package com.dz.coop.module.model;

import com.dz.coop.module.constant.InterfaceTypeEnum;

import java.util.Date;

/**
 * @author panqz 2018-10-26 7:53 PM
 */
public class Partner {

    private Long id;

    private String name;

    private String apiKey;

    private Integer isSync;

    /**
     * 是否自有，0：否，1：是（废弃）
     */
    private Integer isOwch;

    /**
     * 是否自有，0：否，1：是
     */
    private Integer isOwner;

    private Date ctime;

    private Date utime;

    private String aliasId;

    /**
     * 书籍类型：1.文本，2.有声读物
     */
    private Integer bookType;

    /**
     * 是否我方规范，1：是，2：否
     */
    private Integer isOurStandard;

    /**
     * 是否IP白名单，0：否，1：是
     */
    private Integer isIpWhitelist;

    private PartnerUrl url = new PartnerUrl();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }

    public Integer getIsOwch() {
        return isOwch;
    }

    public void setIsOwch(Integer isOwch) {
        this.isOwch = isOwch;
    }

    public Integer getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Integer isOwner) {
        this.isOwner = isOwner;
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

    public PartnerUrl getUrl() {
        return url;
    }

    public void setUrl(PartnerUrl url) {
        this.url = url;
    }

    /**
     * 是否自有，true是
     * @return
     */
    public boolean isInnerDz() {
        return isOwner == 1;
    }

    public String getBookListUrl() {
        return url.getBookListUrl();
    }

    public String getBookInfoUrl() {
        return url.getBookInfoUrl();
    }

    public String getChapterListUrl() {
        return url.getChapterListUrl();
    }

    public String getChapterInfoUrl() {
        return url.getChapterInfoUrl();
    }

    public String getCategoryListUrl() {
        return url.getCategoryListUrl();
    }

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }

    public boolean isSync() {
        return getIsSync() == 1;
    }

    public Integer getBookType() {
        return bookType;
    }

    public void setBookType(Integer bookType) {
        this.bookType = bookType;
    }

    public Integer getIsOurStandard() {
        return isOurStandard;
    }

    /**
     * 是否按照我方规范，true是
     * @return
     */
    public boolean isDzStandard() {
        return isOurStandard == InterfaceTypeEnum.OUR_STANDARD.getType();
    }

    public void setIsOurStandard(Integer isOurStandard) {
        this.isOurStandard = isOurStandard;
    }

    public Integer getIsIpWhitelist() {
        return isIpWhitelist;
    }

    public void setIsIpWhitelist(Integer isIpWhitelist) {
        this.isIpWhitelist = isIpWhitelist;
    }

}
