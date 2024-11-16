package com.dz.coop.module.model.vo;

import java.util.List;

public class QuYueWangResponseInfo {

	private List<QuYueWangBook> books = null;//书籍列表

	private List<QuYueWangChapterList> chapterList = null;//章节列表信息

	public List<QuYueWangBook> getBooks() {
		return books;
	}

	public void setBooks(List<QuYueWangBook> books) {
		this.books = books;
	}

	public List<QuYueWangChapterList> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<QuYueWangChapterList> chapterList) {
		this.chapterList = chapterList;
	}


}
