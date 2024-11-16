package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class KangShuWangBookInfo {
	private String id;//小说ID
	private String bookName;//小说名称
	private String subTitle;//小说副标题
	private String detail;//小说简介
	private String bookType;//小说类别ID
	private String keyWord;//关键字（不存在为0）
	private String bookStatus;//小说状态（0：连载 1：完本）
	private Integer size;//小说字数
	private String author;//作者
	private Integer isVip;//1:vip 0:非vip
	private Integer weekVisit;//周点击
	private Integer monthVisit;//月点击
	private Integer allVisit;//总点击
	private String bookTypeName;//分类名称
	private String imagePath;//图片地址（大）600*800
	private String imageMidPath;//图片地址（中）180*240
	private String imageMinPath;//图片地址（小）60*80
	private Integer isFree;//1:全本收费  0：按章收费
	private Integer maxFree;//最大免费章节数
	private Double price;//全本收费价钱（在isFree为1的时候有效）单位分
	private Integer chapterCount;//章节数
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(String bookStatus) {
		this.bookStatus = bookStatus;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getIsVip() {
		return isVip;
	}

	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}

	public Integer getWeekVisit() {
		return weekVisit;
	}

	public void setWeekVisit(Integer weekVisit) {
		this.weekVisit = weekVisit;
	}

	public Integer getMonthVisit() {
		return monthVisit;
	}

	public void setMonthVisit(Integer monthVisit) {
		this.monthVisit = monthVisit;
	}

	public Integer getAllVisit() {
		return allVisit;
	}

	public void setAllVisit(Integer allVisit) {
		this.allVisit = allVisit;
	}

	public String getBookTypeName() {
		return bookTypeName;
	}

	public void setBookTypeName(String bookTypeName) {
		this.bookTypeName = bookTypeName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageMidPath() {
		return imageMidPath;
	}

	public void setImageMidPath(String imageMidPath) {
		this.imageMidPath = imageMidPath;
	}

	public String getImageMinPath() {
		return imageMinPath;
	}

	public void setImageMinPath(String imageMinPath) {
		this.imageMinPath = imageMinPath;
	}

	public Integer getIsFree() {
		return isFree;
	}

	public void setIsFree(Integer isFree) {
		this.isFree = isFree;
	}

	public Integer getMaxFree() {
		return maxFree;
	}

	public void setMaxFree(Integer maxFree) {
		this.maxFree = maxFree;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Integer getChapterCount() {
		return chapterCount;
	}

	public void setChapterCount(Integer chapterCount) {
		this.chapterCount = chapterCount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
