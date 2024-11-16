package com.dz.coop.module.model.cp;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CPChapter {

	private String id;

	/**
	 * 章节名称
	 */
	private String name;

	/**
	 * 章节内容
	 */
	private String content;

	/**
	 * 是否收费：0-免费，1-收费
	 */
	private Integer isFree;

	/**
	 * 文本书籍章节字数
	 */
	private Integer wordNum;

	private String bookId;

	private String volumeId;

	/**
	 * 音频文件时长（单位：秒）
	 */
	private Integer duration;

	/**
	 * 音频文件大小（单位：KB）
	 */
	private Integer size;

	/**
	 * 音频文件格式（如：mp3、m4a等）
	 */
	private String format;
	/**
	 * 最近更新时间
	 */
	private String lastUtime;

	public CPChapter() {
	}

	public CPChapter(String id, String name) {
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIsFree() {
		return isFree;
	}

	public void setIsFree(Integer isFree) {
		this.isFree = isFree;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public Integer getWordNum() {
		return wordNum;
	}

	public void setWordNum(Integer wordNum) {
		this.wordNum = wordNum;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

    public String getLastUtime() {
        return lastUtime;
    }

    public void setLastUtime(String lastUtime) {
        this.lastUtime = lastUtime;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
