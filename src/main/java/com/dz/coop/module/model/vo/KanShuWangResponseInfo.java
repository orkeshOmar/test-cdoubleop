package com.dz.coop.module.model.vo;

import java.util.List;

public class KanShuWangResponseInfo {

	private List<KanShuWangBook> books = null;//书籍列表

	private KangShuWangBookInfo bookInfo= null;//书籍基本信息
	
	private List<KanShuWangChapterList> chapterList = null;//章节列表信息
	private Integer lastnum;//剩余章节数量（功能同checkUp接口）

	private KanShuWangChapterContent chapter = null;//章节内容

	private String code;//错误代码

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	//根据错误代码，生成错误消息
	public String getErrorInfo(){
		String result = "";
		switch(this.code){
			case "0001":
				result = "资源为空";
				break;
			case "0010":
				result = "参数错误(小说ID或章节ID非int型等)";
				break;
			case "0011":
				result = "鉴权失败（CP方IP鉴权）";
				break;
			case "0100":
				result = "服务器错误";
				break;
			case "0101":
				result = "type错误zip和xml";
				break;
			case "0110":
				result ="无此书权限";
				break;
		}
		return result;
	}

	public List<KanShuWangBook> getBooks() {
		return books;
	}

	public void setBooks(List<KanShuWangBook> books) {
		this.books = books;
	}

	public KangShuWangBookInfo getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(KangShuWangBookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}

	public List<KanShuWangChapterList> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<KanShuWangChapterList> chapterList) {
		this.chapterList = chapterList;
	}
	
	public Integer getLastnum() {
		return lastnum;
	}

	public void setLastnum(Integer lastnum) {
		this.lastnum = lastnum;
	}

	public KanShuWangChapterContent getChapter() {
		return chapter;
	}

	public void setChapter(KanShuWangChapterContent chapter) {
		this.chapter = chapter;
	}

}
