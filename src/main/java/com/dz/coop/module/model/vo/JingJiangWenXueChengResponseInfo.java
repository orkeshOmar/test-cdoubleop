package com.dz.coop.module.model.vo;

import java.util.List;

public class JingJiangWenXueChengResponseInfo {

	private List<JingJiangWenXueChengBook> books = null;//书籍列表
	
	private List<JingJiangWenXueChengChapterList> chapterList = null;//章节列表信息

	private Integer lastnum;//剩余章节数量（功能同checkUp接口）

	private String errorCode;//错误代码

	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	//根据错误代码，生成错误消息
	public String getErrorInfo(){
		String result = "";
		switch(this.errorCode){
			case "1000":
				result = "请求正常";
				break;
			case "1001":
				result = "Openid不合法";
				break;
			case "1002":
				result = "签名(sign)验证失败";
				break;
			case "1003":
				result = "Ip限制";
				break;
			case "1004":
				result = "暂时没有授权小说";
				break;
			case "1005":
				result ="小说没有授权";
				break;
			case "1006":
				result ="章节信息不存在";
				break;
			case "1007":
				result ="找不到授权小说的连载信息";
				break;
			case "1008":
				result ="小说不存在";
				break;
			case "1009":
				result ="没有获取txt文本权限";
				break;
			case "1100":
				result ="无法找到对应的文本信息";
				break;
			case "1101":
				result ="没有开通榜单权限";
				break;
			case "2000":
				result ="未知错误";
				break;
		}
		return result;
	}


	public List<JingJiangWenXueChengBook> getBooks() {
		return books;
	}

	public void setBooks(List<JingJiangWenXueChengBook> books) {
		this.books = books;
	}

	public List<JingJiangWenXueChengChapterList> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<JingJiangWenXueChengChapterList> chapterList) {
		this.chapterList = chapterList;
	}


	public Integer getLastnum() {
		return lastnum;
	}

	public void setLastnum(Integer lastnum) {
		this.lastnum = lastnum;
	}


}
