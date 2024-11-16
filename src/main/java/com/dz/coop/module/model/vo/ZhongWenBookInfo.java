package com.dz.coop.module.model.vo;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ZhongWenBookInfo {
	private String cname;//书籍所属分类
	private String bookname;//书籍名称
	private String bookid;//书籍ID
	private String bookpic;//书籍封面地址
	private String zzjs;//书籍简介
	private String authorname;//作者
	private String createtime;//书籍创建时间
	private String bksize;//书籍大小

	private Long wordcount;//书籍字数
	private Long weekvisit;//周点击数
	private Long monthvisit;//月点击数
	private Long allvisit;//总点击数
	private String writestatus;//是否完结， 1表示完结
	private String license;//是否收费
	private String wholeprice;//整本书价格，如果是出版物给出具体价格，0表示不是出版物
	private String booktype;//书籍类型，1表示出版物，0表示原创

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getBookid() {
		return bookid;
	}

	public void setBookid(String bookid) {
		this.bookid = bookid;
	}

	public String getBookpic() {
		return bookpic;
	}

	public void setBookpic(String bookpic) {
		this.bookpic = bookpic;
	}

	public String getZzjs() {
		return zzjs;
	}

	public void setZzjs(String zzjs) {
		this.zzjs = zzjs;
	}

	public String getAuthorname() {
		return authorname;
	}

	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getBksize() {
		return bksize;
	}

	public void setBksize(String bksize) {
		this.bksize = bksize;
	}

	public Long getWordcount() {
		return wordcount;
	}

	public void setWordcount(Long wordcount) {
		this.wordcount = wordcount;
	}

	public Long getWeekvisit() {
		return weekvisit;
	}

	public void setWeekvisit(Long weekvisit) {
		this.weekvisit = weekvisit;
	}

	public Long getMonthvisit() {
		return monthvisit;
	}

	public void setMonthvisit(Long monthvisit) {
		this.monthvisit = monthvisit;
	}

	public Long getAllvisit() {
		return allvisit;
	}

	public void setAllvisit(Long allvisit) {
		this.allvisit = allvisit;
	}

	public String getWritestatus() {
		return writestatus;
	}

	public void setWritestatus(String writestatus) {
		this.writestatus = writestatus;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getWholeprice() {
		return wholeprice;
	}

	public void setWholeprice(String wholeprice) {
		this.wholeprice = wholeprice;
	}

	public String getBooktype() {
		return booktype;
	}

	public void setBooktype(String booktype) {
		this.booktype = booktype;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
