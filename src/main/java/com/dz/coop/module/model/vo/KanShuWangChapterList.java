package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class KanShuWangChapterList {
	private String chapterId;//章节ID
	private String chapterName;//章节名称
	private Integer chapterSize;//章节大小 
	private Integer isVip;//是否收费章节1：收费 0：免费
	private Double price;//价格单位（分）（只有在isVip为1时有效）

	public String getChapterId() {
		return chapterId;
	}

	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public Integer getChapterSize() {
		return chapterSize;
	}

	public void setChapterSize(Integer chapterSize) {
		this.chapterSize = chapterSize;
	}

	public Integer getIsVip() {
		return isVip;
	}

	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
