package com.dz.coop.module.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 掌中章节
 * @author kangyf
 * @date 2016-09-05
 */
public class ZhangZhongChapter {
	
	private Long bookid;//书籍ID（long）
	private Long chapterid;//章节ID（long）
	private String chaptername;//章节名称
	private String texturl;//下载地址
	private String updatetime;//更改时间（Datetime）
	private int vip;//状态：0：非vip章节；1：vip章节（int）

	public Long getBookid() {
		return bookid;
	}

	public void setBookid(Long bookid) {
		this.bookid = bookid;
	}

	public Long getChapterid() {
		return chapterid;
	}

	public void setChapterid(Long chapterid) {
		this.chapterid = chapterid;
	}

	public String getChaptername() {
		return chaptername;
	}

	public void setChaptername(String chaptername) {
		this.chaptername = chaptername;
	}

	public String getTexturl() {
		return texturl;
	}

	public void setTexturl(String texturl) {
		this.texturl = texturl;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
