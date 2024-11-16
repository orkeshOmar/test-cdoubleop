package com.dz.coop.module.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 逐浪书籍
 * @author Administrator
 * @date 2016-04-22
 */

public class ZhuLangBook {

	private String bk_id;//书号
	private String bk_cre_date;//创建时间 yyyy-mm-dd xx:xx:xx
	private String bk_name;//书名
	private String bk_author_id;//作者ID
	private String bk_author;//作者名称
	private String bk_size;//总字数
	private String bk_intro;//作品简介
	private String bk_visit_week;//本周点击数
	private String bk_visit_mouth;//本月点击数
	private String bk_visit_all;//总点击数
	private String bk_com_week;//本周推荐数
	private String bk_com_mouth;//本月推荐数
	private String bk_com_all;//总推荐数
	private String bk_class_a;//一级分类代码
	private String bk_class_a_name;//一级分类名称
	private String bk_public_update;//公众章节最新更新时间yyyy-mm-dd xx:xx:xx
	private String bk_public_name;//公众章节最新更新名称
	private String bk_public_ch_id;//公众章节最新更新ID
	private String bk_vip_update;//VIP章节最新更新时间yyyy-mm-dd xx:xx:xx
	private String bk_vip_name;//VIP章节最新更新名称
	private String bk_vip_ch_id;//VIP章节最新更新ID
	private String bk_last_ch_update;//章节最新更新时间yyyy-mm-dd xx:xx:xx ，公众和VIP中更近者
	private String bk_last_ch_id;//章节最新更新名称
	private String bk_last_ch_name;//章节更新ID
	private String bk_last_ch_url;//最新章节阅读url地址
	private String bk_fullflag;//状态，’0’连载；’1’完本；’2’封笔
	private String bk_cover;//作品封面图片url地址
	private String bk_flower_total;//总鲜花数
	private String bk_flower_month;//当月鲜花数
	private String ch_total;//章节总数

	public ZhuLangBook() {
		// TODO Auto-generated constructor stub
	}
	
	public String getBk_id() {
		return bk_id;
	}

	public void setBk_id(String bk_id) {
		this.bk_id = bk_id;
	}

	public String getBk_cre_date() {
		return bk_cre_date;
	}

	public void setBk_cre_date(String bk_cre_date) {
		this.bk_cre_date = bk_cre_date;
	}

	public String getBk_name() {
		return bk_name;
	}

	public void setBk_name(String bk_name) {
		this.bk_name = bk_name;
	}

	public String getBk_author_id() {
		return bk_author_id;
	}

	public void setBk_author_id(String bk_author_id) {
		this.bk_author_id = bk_author_id;
	}

	public String getBk_author() {
		return bk_author;
	}

	public void setBk_author(String bk_author) {
		this.bk_author = bk_author;
	}

	public String getBk_size() {
		return bk_size;
	}

	public void setBk_size(String bk_size) {
		this.bk_size = bk_size;
	}

	public String getBk_intro() {
		return bk_intro;
	}

	public void setBk_intro(String bk_intro) {
		this.bk_intro = bk_intro;
	}

	public String getBk_visit_week() {
		return bk_visit_week;
	}

	public void setBk_visit_week(String bk_visit_week) {
		this.bk_visit_week = bk_visit_week;
	}

	public String getBk_visit_mouth() {
		return bk_visit_mouth;
	}

	public void setBk_visit_mouth(String bk_visit_mouth) {
		this.bk_visit_mouth = bk_visit_mouth;
	}

	public String getBk_visit_all() {
		return bk_visit_all;
	}

	public void setBk_visit_all(String bk_visit_all) {
		this.bk_visit_all = bk_visit_all;
	}

	public String getBk_com_week() {
		return bk_com_week;
	}

	public void setBk_com_week(String bk_com_week) {
		this.bk_com_week = bk_com_week;
	}

	public String getBk_com_mouth() {
		return bk_com_mouth;
	}

	public void setBk_com_mouth(String bk_com_mouth) {
		this.bk_com_mouth = bk_com_mouth;
	}

	public String getBk_com_all() {
		return bk_com_all;
	}

	public void setBk_com_all(String bk_com_all) {
		this.bk_com_all = bk_com_all;
	}

	public String getBk_class_a() {
		return bk_class_a;
	}

	public void setBk_class_a(String bk_class_a) {
		this.bk_class_a = bk_class_a;
	}

	public String getBk_class_a_name() {
		return bk_class_a_name;
	}

	public void setBk_class_a_name(String bk_class_a_name) {
		this.bk_class_a_name = bk_class_a_name;
	}

	public String getBk_public_update() {
		return bk_public_update;
	}

	public void setBk_public_update(String bk_public_update) {
		this.bk_public_update = bk_public_update;
	}

	public String getBk_public_name() {
		return bk_public_name;
	}

	public void setBk_public_name(String bk_public_name) {
		this.bk_public_name = bk_public_name;
	}

	public String getBk_public_ch_id() {
		return bk_public_ch_id;
	}

	public void setBk_public_ch_id(String bk_public_ch_id) {
		this.bk_public_ch_id = bk_public_ch_id;
	}

	public String getBk_vip_update() {
		return bk_vip_update;
	}

	public void setBk_vip_update(String bk_vip_update) {
		this.bk_vip_update = bk_vip_update;
	}

	public String getBk_vip_name() {
		return bk_vip_name;
	}

	public void setBk_vip_name(String bk_vip_name) {
		this.bk_vip_name = bk_vip_name;
	}

	public String getBk_vip_ch_id() {
		return bk_vip_ch_id;
	}

	public void setBk_vip_ch_id(String bk_vip_ch_id) {
		this.bk_vip_ch_id = bk_vip_ch_id;
	}

	public String getBk_last_ch_update() {
		return bk_last_ch_update;
	}

	public void setBk_last_ch_update(String bk_last_ch_update) {
		this.bk_last_ch_update = bk_last_ch_update;
	}

	public String getBk_last_ch_id() {
		return bk_last_ch_id;
	}

	public void setBk_last_ch_id(String bk_last_ch_id) {
		this.bk_last_ch_id = bk_last_ch_id;
	}

	public String getBk_last_ch_name() {
		return bk_last_ch_name;
	}

	public void setBk_last_ch_name(String bk_last_ch_name) {
		this.bk_last_ch_name = bk_last_ch_name;
	}

	public String getBk_last_ch_url() {
		return bk_last_ch_url;
	}

	public void setBk_last_ch_url(String bk_last_ch_url) {
		this.bk_last_ch_url = bk_last_ch_url;
	}

	public String getBk_fullflag() {
		return bk_fullflag;
	}

	public void setBk_fullflag(String bk_fullflag) {
		this.bk_fullflag = bk_fullflag;
	}

	public String getBk_cover() {
		return bk_cover;
	}

	public void setBk_cover(String bk_cover) {
		this.bk_cover = bk_cover;
	}

	public String getBk_flower_total() {
		return bk_flower_total;
	}

	public void setBk_flower_total(String bk_flower_total) {
		this.bk_flower_total = bk_flower_total;
	}

	public String getBk_flower_month() {
		return bk_flower_month;
	}

	public void setBk_flower_month(String bk_flower_month) {
		this.bk_flower_month = bk_flower_month;
	}

	public String getCh_total() {
		return ch_total;
	}

	public void setCh_total(String ch_total) {
		this.ch_total = ch_total;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}