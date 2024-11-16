package com.dz.coop.module.model;

/***
 * 纵横章节
 * @author zuorz
 */
public class ZongHengChapter {
	
	private String chapterId;
	private String chapterName;
	private String content;
	private String tomeId;
	private String tomeName;
	private String wordNum;
	private String createTimeStr;
	private String updateTimeStr;
	private String status;
	private String ordernum;
	private String level;
	private String priceUnit;
	private String discount;
	private String price;
	
	/**
	 * ZongHengServiceImpl getSourceZHChapterWithContent 抛异常，
	 * no suitable creator method found to deserialize from JSON String
	 * 所以追加构造方法
	 */
	public ZongHengChapter() {
		// TODO Auto-generated constructor stub
	}

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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTomeId() {
		return tomeId;
	}
	public void setTomeId(String tomeId) {
		this.tomeId = tomeId;
	}
	
	public String getTomeName() {
		return tomeName;
	}

	public void setTomeName(String tomeName) {
		this.tomeName = tomeName;
	}

	public String getWordNum() {
		return wordNum;
	}
	public void setWordNum(String wordNum) {
		this.wordNum = wordNum;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getUpdateTimeStr() {
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getPriceUnit() {
		return priceUnit;
	}
	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	

}
