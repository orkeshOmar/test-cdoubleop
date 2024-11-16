package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class QuYueWangBookInfo {
	private String bkId;//作品书号
	private String bkName;//作品名称
	private String bkEpName;
	private String author;//作者名
	private String authorIntro;//作者简介
	private String fullFlag;//是否完本 0 连载 1完本

	private Integer size;//字数
	private String tag;//书关键词标签
	private String vipcharge;
	private String intro;//作品介绍
	private String bkUrl;//小说网址
	private String coverUrl;//封面网址
	private String coverBigUrl;//封面大图网址
	private String classId;//一级分类名称
	private String classId2;//二级分类名称
	private String pushName;//推荐语
	private String postTime;//作品创建时间
	
	public String getBkId() {
		return bkId;
	}

	public void setBkId(String bkId) {
		this.bkId = bkId;
	}

	public String getBkName() {
		return bkName;
	}

	public void setBkName(String bkName) {
		this.bkName = bkName;
	}
	
	public String getBkEpName() {
		return bkEpName;
	}

	public void setBkEpName(String bkEpName) {
		this.bkEpName = bkEpName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorIntro() {
		return authorIntro;
	}

	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}

	public String getFullFlag() {
		return fullFlag;
	}

	public void setFullFlag(String fullFlag) {
		this.fullFlag = fullFlag;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getVipcharge() {
		return vipcharge;
	}

	public void setVipcharge(String vipcharge) {
		this.vipcharge = vipcharge;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getBkUrl() {
		return bkUrl;
	}

	public void setBkUrl(String bkUrl) {
		this.bkUrl = bkUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getCoverBigUrl() {
		return coverBigUrl;
	}

	public void setCoverBigUrl(String coverBigUrl) {
		this.coverBigUrl = coverBigUrl;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassId2() {
		return classId2;
	}

	public void setClassId2(String classId2) {
		this.classId2 = classId2;
	}

	public String getPushName() {
		return pushName;
	}

	public void setPushName(String pushName) {
		this.pushName = pushName;
	}

	public String getPostTime() {
		return postTime;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
