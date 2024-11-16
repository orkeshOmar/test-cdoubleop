package com.dz.coop.module.model.cp;

import com.dz.coop.module.model.AudioTrailer;
import com.dz.coop.module.model.PartnerCategoryModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 掌阅书籍
 *
 * @author zuorz
 */
public class CPBook {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String author;

    private String brief;

    /**
     * 完本状态，0：未完本，1：完本
     */
    private String completeStatus;

    @NotNull
    private String cover;

    private String tag;

    private String category;

    private String readableCategory;

    private PartnerCategoryModel partnerCategoryModel;

    private String bookId;

    private Long cpId;

    private String price;

    private String recommendPrice;

    /**
     * 收费类型：本，章
     */
    private String unit;

    private boolean isLastPage;

    private String extend;

    /**
     * 点赞数
     */
    private Long praiseNum;

    private List<AudioTrailer> audioTrailers;

    List<CPVolume> cpVolumeList = new ArrayList<>();

    public CPBook() {
    }

    public CPBook(String id) {
        this.id = id;
    }

    public CPBook(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CPBook(String id, String name, String author, String brief, String completeStatus, String cover, String category, String tag) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.brief = brief;
        this.completeStatus = completeStatus;
        this.cover = cover;
        this.category = category;
        this.tag = tag;
    }

    public List<AudioTrailer> getAudioTrailers() {
        return audioTrailers;
    }

    public void setAudioTrailers(List<AudioTrailer> audioTrailers) {
        this.audioTrailers = audioTrailers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(String completeStatus) {
        this.completeStatus = completeStatus;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<CPVolume> getCpVolumeList() {
        return cpVolumeList;
    }

    public void setCpVolumeList(List<CPVolume> cpVolumeList) {
        this.cpVolumeList = cpVolumeList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReadableCategory() {
        return readableCategory;
    }

    public void setReadableCategory(String readableCategory) {
        this.readableCategory = readableCategory;
    }

    public void resetAndAddCpVolume(CPVolume cpVolume) {
        cpVolumeList.clear();
        cpVolumeList.add(cpVolume);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Long getCpId() {
        return cpId;
    }

    public void setCpId(Long cpId) {
        this.cpId = cpId;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isFinished() {
        return StringUtils.equals(getCompleteStatus(), "1");
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

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Long getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(Long praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public PartnerCategoryModel getPartnerCategoryModel() {
        return partnerCategoryModel;
    }

    public void setPartnerCategoryModel(PartnerCategoryModel partnerCategoryModel) {
        this.partnerCategoryModel = partnerCategoryModel;
    }
}
