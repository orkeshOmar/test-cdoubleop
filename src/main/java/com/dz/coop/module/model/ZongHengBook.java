package com.dz.coop.module.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 纵横书籍
 */

public class ZongHengBook {

	private String bookId;
	private String bookName;
	private String categoryId;
	private String categoryName;
	private String parentCategoryId;
	private String parentCategoryName;
	private String authorId;
	private String authorName;
	private String totalRecommend;
	private String keywords;//关键词
	private String serialStatus;//0连载 1完本
	private String totalReject;
	private String totalClick;
	private String totalFavorite;
	private String level;
	private String description;
	private String coverUrl;
	private String status;
	private String createTime;
	private String updateTime;
	private String totalWord;
	private String updateChapterId;
	private String updateChapterName;
	private String authorization;

	public ZongHengBook() {
		// TODO Auto-generated constructor stub
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

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(String parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getParentCategoryName() {
		return parentCategoryName;
	}

	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getTotalRecommend() {
		return totalRecommend;
	}

	public void setTotalRecommend(String totalRecommend) {
		this.totalRecommend = totalRecommend;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSerialStatus() {
		return serialStatus;
	}

	public void setSerialStatus(String serialStatus) {
		this.serialStatus = serialStatus;
	}

	public String getTotalReject() {
		return totalReject;
	}

	public void setTotalReject(String totalReject) {
		this.totalReject = totalReject;
	}

	public String getTotalClick() {
		return totalClick;
	}

	public void setTotalClick(String totalClick) {
		this.totalClick = totalClick;
	}

	public String getTotalFavorite() {
		return totalFavorite;
	}

	public void setTotalFavorite(String totalFavorite) {
		this.totalFavorite = totalFavorite;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getTotalWord() {
		return totalWord;
	}

	public void setTotalWord(String totalWord) {
		this.totalWord = totalWord;
	}

	public String getUpdateChapterId() {
		return updateChapterId;
	}

	public void setUpdateChapterId(String updateChapterId) {
		this.updateChapterId = updateChapterId;
	}

	public String getUpdateChapterName() {
		return updateChapterName;
	}

	public void setUpdateChapterName(String updateChapterName) {
		this.updateChapterName = updateChapterName;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}