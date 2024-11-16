package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class QuYueWangChapterList {
	private String rollId;//卷id
	private String chId;//章节id
	private int type;//类型 0章节 1卷
	private String vip;//是否vip章节 0公众章节 1vip章节（若是卷，则为空）
	private int size;//章节大小
	private String postTime;//发布时间
	private String name;//章节名或卷名
	private Double priceunit;//千字单价,如是免费章节则为空
	private Double price;//本章总价,如是免费章节则为空
	
	public String getRollId() {
		return rollId;
	}

	public void setRollId(String rollId) {
		this.rollId = rollId;
	}

	public String getChId() {
		return chId;
	}

	public void setChId(String chId) {
		this.chId = chId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getPostTime() {
		return postTime;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPriceunit() {
		return priceunit;
	}

	public void setPriceunit(Double priceunit) {
		this.priceunit = priceunit;
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
