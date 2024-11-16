package com.dz.coop.module.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 逐浪章节
 * @author kangyf
 * @date 2016-04-22
 */
public class ZhuLangChapter {
	
	private String ch_roll_name;//所属卷名称
	private String ch_roll;//所属卷编号
	private String ch_id;//章节ID
	private String ch_name;//章节名称
	private String ch_cre_time;//章节创建时间yyyy-mm-dd xx:xx:xx
	private String ch_update;//章节最后更新时间yyyy-mm-dd xx:xx:xx
	private String ch_size;//章节字数
	private String ch_vip;//是否为VIP章节，0：公众章节；1：VIP章节

	/**
	 * ZongHengServiceImpl getSourceZHChapterWithContent 抛异常，
	 * no suitable creator method found to deserialize from JSON String
	 * 所以追加构造方法
	 */
	public ZhuLangChapter() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCh_roll_name() {
		return ch_roll_name;
	}

	public void setCh_roll_name(String ch_roll_name) {
		this.ch_roll_name = ch_roll_name;
	}

	public String getCh_roll() {
		return ch_roll;
	}

	public void setCh_roll(String ch_roll) {
		this.ch_roll = ch_roll;
	}

	public String getCh_id() {
		return ch_id;
	}

	public void setCh_id(String ch_id) {
		this.ch_id = ch_id;
	}

	public String getCh_name() {
		return ch_name;
	}

	public void setCh_name(String ch_name) {
		this.ch_name = ch_name;
	}

	public String getCh_cre_time() {
		return ch_cre_time;
	}

	public void setCh_cre_time(String ch_cre_time) {
		this.ch_cre_time = ch_cre_time;
	}

	public String getCh_update() {
		return ch_update;
	}

	public void setCh_update(String ch_update) {
		this.ch_update = ch_update;
	}

	public String getCh_size() {
		return ch_size;
	}

	public void setCh_size(String ch_size) {
		this.ch_size = ch_size;
	}

	public String getCh_vip() {
		return ch_vip;
	}

	public void setCh_vip(String ch_vip) {
		this.ch_vip = ch_vip;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
