package com.dz.coop.module.model.vo;

import java.util.List;

public class ZhongWenResponseInfo {

	private List<ZhongWenBook> books = null;//书籍列表
	private ZhongWenBookInfo bookInfo;//书籍基本信息
	private List<ZhongWenVolume> volumes = null;//章节目录
	public List<ZhongWenBook> getBooks() {
		return books;
	}
	public void setBooks(List<ZhongWenBook> books) {
		this.books = books;
	}
	public ZhongWenBookInfo getBookInfo() {
		return bookInfo;
	}
	public void setBookInfo(ZhongWenBookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}
	public List<ZhongWenVolume> getVolumes() {
		return volumes;
	}
	public void setVolumes(List<ZhongWenVolume> volumes) {
		this.volumes = volumes;
	}


}
