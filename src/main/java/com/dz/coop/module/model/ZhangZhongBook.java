package com.dz.coop.module.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 掌中文学
 * @author kangyf
 */
public class ZhangZhongBook {

	private Long bookid;//书籍ID
	private String bookname;	//书籍名称
	private String latestupdatetime;//书籍更新时间
	private String authorname;//作者
	private int length;//书籍长度
	private String imgsrc;//封面URL
	private String introduce;//简介<200字
	private String createtime;//创建时间
	private String cname;//分类名称
	private String updatetime;//信息更改时间
	private String chapterupdatetime;//章节更新时间
	private int status;//状态：0：删除；1：正常（int）
	private int writestatus;//状态：0：连载；1：完本（int）

	public Long getBookid() {
		return bookid;
	}

	public void setBookid(Long bookid) {
		this.bookid = bookid;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getLatestupdatetime() {
		return latestupdatetime;
	}

	public void setLatestupdatetime(String latestupdatetime) {
		this.latestupdatetime = latestupdatetime;
	}

	public String getAuthorname() {
		return authorname;
	}

	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getImgsrc() {
		return imgsrc;
	}

	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getChapterupdatetime() {
		return chapterupdatetime;
	}

	public void setChapterupdatetime(String chapterupdatetime) {
		this.chapterupdatetime = chapterupdatetime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWritestatus() {
		return writestatus;
	}

	public void setWritestatus(int writestatus) {
		this.writestatus = writestatus;
	}

	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	
}
