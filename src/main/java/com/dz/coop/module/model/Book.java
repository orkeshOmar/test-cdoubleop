package com.dz.coop.module.model;

import com.alibaba.druid.util.StringUtils;
import com.dz.coop.common.annotation.Escape;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author panqz 2018-10-26 7:31 PM
 */

public class Book {

    public static final String END_PRICE = "5.00";

    public static final String SERIAL_PRICE = "0.15";

    /**
     * 有声读物连载单章默认价格
     */
    public static final String AUDIO_SERIAL_PRICE = "0.20";

    public static final String END_UNIT = "本";

    public static final String SERIAL_UNIT = "章";

    public static final String DEFAULT_BOOK_TYPE = "113";

    public static final String COMPLETE_STATUS_END = "完本";

    public static final String COMPLETE_STATUS_SERIAL = "连载";

    public static final String CHANNEL_ONLINE = "channel_online";

    /**
     * 蜻蜓FM书籍下架
     */
    public static final String CHANNEL_OFFLINE = "channel_offline";

    private String bookId;

    @NotNull
    private String cpBookId;

    @NotNull
    private String partnerId;

    @Escape
    @NotNull
    private String name;

    @Escape
    @NotNull
    private String author;

    @Escape
    @NotNull
    private String introduction;

    @Escape
    private String tag;

    @NotNull
    private String cover;

    @NotNull
    private String status;

    List<Volume> volumes;

    private Integer id;

    private Integer isSync;

    private Long lastChapterId;

    private String lastChapterName;

    private Date lastChapterUtime;

    private Integer from = 2;

    private Integer payType = 2;

    private Integer payTypeCnf = 2;

    /**
     * 默认期刊
     */
    private String bookTypeId = DEFAULT_BOOK_TYPE;

    private String price = SERIAL_PRICE;

    private String recommendPrice = SERIAL_PRICE;

    private String unit;

    private Integer marketStatus = 8;

    private String coverWap;

    private String coverWww;

    private Long praiseNum;

    private String clickNum;

    private String totalChapterNum;

    private Date createTime;

    private Date updateTime;

    private String totalWordSize;

    private Chapter lastChapter;

    /**
     * 书籍类型，1：文本，2：有声读物
     */
    private Integer bookType;

    private String extend;

    public Book() {
    }

    public Integer getBookType() {
        return bookType;
    }

    public void setBookType(Integer bookType) {
        this.bookType = bookType;
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

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }

    public Long getLastChapterId() {
        return lastChapterId;
    }

    public void setLastChapterId(Long lastChapterId) {
        this.lastChapterId = lastChapterId;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public Date getLastChapterUtime() {
        return lastChapterUtime;
    }

    public void setLastChapterUtime(Date lastChapterUtime) {
        this.lastChapterUtime = lastChapterUtime;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayTypeCnf() {
        return payTypeCnf;
    }

    public void setPayTypeCnf(Integer payTypeCnf) {
        this.payTypeCnf = payTypeCnf;
    }

    public String getBookTypeId() {
        return bookTypeId;
    }

    public void setBookTypeId(String bookTypeId) {
        this.bookTypeId = bookTypeId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRecommendPrice() {
        return recommendPrice;
    }

    public void setRecommendPrice(String recommendPrice) {
        this.recommendPrice = recommendPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(Integer marketStatus) {
        this.marketStatus = marketStatus;
    }

    public String getCoverWap() {
        return coverWap;
    }

    public void setCoverWap(String coverWap) {
        this.coverWap = coverWap;
    }

    public String getCoverWww() {
        return coverWww;
    }

    public void setCoverWww(String coverWww) {
        this.coverWww = coverWww;
    }

    public Long getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(Long praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getClickNum() {
        return clickNum;
    }

    public void setClickNum(String clickNum) {
        this.clickNum = clickNum;
    }

    public String getTotalChapterNum() {
        return totalChapterNum;
    }

    public void setTotalChapterNum(String totalChapterNum) {
        this.totalChapterNum = totalChapterNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTotalWordSize() {
        return totalWordSize;
    }

    public void setTotalWordSize(String totalWordSize) {
        this.totalWordSize = totalWordSize;
    }

    public boolean isEnd() {
        return StringUtils.equals(getStatus(), "完本");
    }

    public static String getEndPrice() {
        return END_PRICE;
    }

    public static String getSerialPrice() {
        return SERIAL_PRICE;
    }

    public static String getEndUnit() {
        return END_UNIT;
    }

    public static String getSerialUnit() {
        return SERIAL_UNIT;
    }

    public static String getDefaultBookType() {
        return DEFAULT_BOOK_TYPE;
    }

    public Chapter getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(Chapter lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean isOnshelve() {
        return this.marketStatus == 1;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

}
