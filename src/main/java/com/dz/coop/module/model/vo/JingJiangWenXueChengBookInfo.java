package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JingJiangWenXueChengBookInfo {
	private String novelid;//小说ID
	private String novelname;//小说名称
	private String authorname;//作者
	private Integer novelsize;//字数
	private String novelclass;//章分类
	private String novelintro;//品的简单介绍，注意其中带html代码
	private String novelintroshort;//作品的一句话介绍
	private String publishdate;//作品的发布日期
	private String renewdate;//不需要	作品最后更新日期
	private Integer befavoritedcount;//作品被收藏数量]
	private Integer novelstep;//更新状态 0为暂停 1为连载中 2为已完结
	private String listurl;//晋江文学城对应的文章链接
	private Integer vipflag;//需要	是否为vip文章 1为vip  0为普通文章
	private String coverurl;//封面对应的url
	private Integer novelscore;//作品在晋江的积分
	private Integer chapterclickright;//文章的点击量
	
	public String getNovelid() {
		return novelid;
	}

	public void setNovelid(String novelid) {
		this.novelid = novelid;
	}

	public String getNovelname() {
		return novelname;
	}

	public void setNovelname(String novelname) {
		this.novelname = novelname;
	}

	public String getAuthorname() {
		return authorname;
	}

	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}

	public Integer getNovelsize() {
		return novelsize;
	}

	public void setNovelsize(Integer novelsize) {
		this.novelsize = novelsize;
	}

	public String getNovelclass() {
		return novelclass;
	}

	public void setNovelclass(String novelclass) {
		this.novelclass = novelclass;
	}

	public String getNovelintro() {
		return novelintro;
	}

	public void setNovelintro(String novelintro) {
		this.novelintro = novelintro;
	}

	public String getNovelintroshort() {
		return novelintroshort;
	}

	public void setNovelintroshort(String novelintroshort) {
		this.novelintroshort = novelintroshort;
	}

	public String getPublishdate() {
		return publishdate;
	}

	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}

	public String getRenewdate() {
		return renewdate;
	}

	public void setRenewdate(String renewdate) {
		this.renewdate = renewdate;
	}

	public Integer getBefavoritedcount() {
		return befavoritedcount;
	}

	public void setBefavoritedcount(Integer befavoritedcount) {
		this.befavoritedcount = befavoritedcount;
	}

	public Integer getNovelstep() {
		return novelstep;
	}

	public void setNovelstep(Integer novelstep) {
		this.novelstep = novelstep;
	}

	public String getListurl() {
		return listurl;
	}

	public void setListurl(String listurl) {
		this.listurl = listurl;
	}

	public Integer getVipflag() {
		return vipflag;
	}

	public void setVipflag(Integer vipflag) {
		this.vipflag = vipflag;
	}

	public String getCoverurl() {
		return coverurl;
	}

	public void setCoverurl(String coverurl) {
		this.coverurl = coverurl;
	}

	public Integer getNovelscore() {
		return novelscore;
	}

	public void setNovelscore(Integer novelscore) {
		this.novelscore = novelscore;
	}

	public Integer getChapterclickright() {
		return chapterclickright;
	}

	public void setChapterclickright(Integer chapterclickright) {
		this.chapterclickright = chapterclickright;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
