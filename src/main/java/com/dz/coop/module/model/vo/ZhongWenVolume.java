package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ZhongWenVolume {
	private String volumename;//书籍卷名称
	private String volumecode;//书籍卷id
	private int volumeorder;//书籍卷排序
	private List<ZhongWenChapter> chapter;
	
	public String getVolumename() {
		return volumename;
	}

	public void setVolumename(String volumename) {
		this.volumename = volumename;
	}

	public String getVolumecode() {
		return volumecode;
	}

	public void setVolumecode(String volumecode) {
		this.volumecode = volumecode;
	}

	public int getVolumeorder() {
		return volumeorder;
	}

	public void setVolumeorder(int volumeorder) {
		this.volumeorder = volumeorder;
	}

	public List<ZhongWenChapter> getChapter() {
		return chapter;
	}

	public void setChapter(List<ZhongWenChapter> chapter) {
		this.chapter = chapter;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
