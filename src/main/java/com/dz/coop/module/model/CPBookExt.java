package com.dz.coop.module.model;

import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;


/**
 * 掌阅书籍
 * @author zuorz
 */
public class CPBookExt {

	private String id;//书籍ID
	private String name;	//书籍名称
	private String author;	//作者
	private String brief;	//简介
	private String complete_status; //完本状态   0:未完本 1 :完本
	private String is_finish;
	private String cover;	//封面URL
	private String category;//分类
	private int totalChapterNum;//总章节数
	private List<String> tag;

	private List <CPVolume> volumeList=new ArrayList<CPVolume>();
	private List <CPChapter> chapterList=new ArrayList<CPChapter>();

	public CPBookExt() {
	}

	public CPBookExt(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	
	public String getComplete_status() {
		return complete_status;
	}
	/**
	 * 完本状态   0:未完本 1 :完本
	 * @param complete_status
	 */
	public void setComplete_status(String complete_status) {
		this.complete_status = complete_status;
	}
	public String getIs_finish() {
		return is_finish;
	}
	public void setIs_finish(String is_finish) {
		this.is_finish = is_finish;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getTotalChapterNum() {
		return totalChapterNum;
	}
	public void setTotalChapterNum(int totalChapterNum) {
		this.totalChapterNum = totalChapterNum;
	}
	
	public List<CPVolume> getVolumeList() {
		return volumeList;
	}
	public void setVolumeList(List<CPVolume> volumeList) {
		this.volumeList = volumeList;
	}
	public List<CPChapter> getChapterList() {
		return chapterList;
	}
	public void setChapterList(List<CPChapter> chapterList) {
		this.chapterList = chapterList;
	}
	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}
}
