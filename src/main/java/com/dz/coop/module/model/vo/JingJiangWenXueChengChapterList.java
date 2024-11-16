package com.dz.coop.module.model.vo;



import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JingJiangWenXueChengChapterList {
	private String novelid;//小说id,小说的唯一标识
	private Integer chaptertype;//章节的类型 0表示章节 1表示卷标
	private String chapterid;//章节id 
	private String chaptername;//章节名（为卷标是次项为卷标名
	private String chapterdate;//更新日期
	private String chaptersize;//章节字数
	private String chapterurl;//在晋江文学城对应的章节内容链接
	private String chapterclick;//在晋江文学城对应的章节内容链接
	private Integer vipflag;//是否为vip章节 1为vip  0为普通章节

	public String getNovelid() {
		return novelid;
	}

	public void setNovelid(String novelid) {
		this.novelid = novelid;
	}

	public Integer getChaptertype() {
		return chaptertype;
	}

	public void setChaptertype(Integer chaptertype) {
		this.chaptertype = chaptertype;
	}

	public String getChapterid() {
		return chapterid;
	}

	public void setChapterid(String chapterid) {
		this.chapterid = chapterid;
	}

	public String getChaptername() {
		return chaptername;
	}

	public void setChaptername(String chaptername) {
		this.chaptername = chaptername;
	}

	public String getChapterdate() {
		return chapterdate;
	}

	public void setChapterdate(String chapterdate) {
		this.chapterdate = chapterdate;
	}

	public String getChaptersize() {
		return chaptersize;
	}

	public void setChaptersize(String chaptersize) {
		this.chaptersize = chaptersize;
	}

	public String getChapterurl() {
		return chapterurl;
	}

	public void setChapterurl(String chapterurl) {
		this.chapterurl = chapterurl;
	}

	public String getChapterclick() {
		return chapterclick;
	}

	public void setChapterclick(String chapterclick) {
		this.chapterclick = chapterclick;
	}

	public Integer getVipflag() {
		return vipflag;
	}

	public void setVipflag(Integer vipflag) {
		this.vipflag = vipflag;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
