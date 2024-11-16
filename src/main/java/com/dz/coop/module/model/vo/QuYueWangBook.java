package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class QuYueWangBook {
	private String bkId;//书号
	private String bkName;//作品名称
	private Integer focalPoint ;//是否重点书
	
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

	public Integer getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(Integer focalPoint) {
		this.focalPoint = focalPoint;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
