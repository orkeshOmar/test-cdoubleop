package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JingJiangWenXueChengBook {
	private String novelid;//小说ID

	public String getNovelid() {
		return novelid;
	}

	public void setNovelid(String novelid) {
		this.novelid = novelid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
