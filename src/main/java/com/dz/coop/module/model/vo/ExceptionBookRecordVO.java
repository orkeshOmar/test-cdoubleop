package com.dz.coop.module.model.vo;

import com.dz.coop.module.model.Book;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @project: coop-client
 * @description: 异常书籍记录对象
 * @author: songwj
 * @date: 2020-01-07 20:36
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class ExceptionBookRecordVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 书籍id
     */
    private String bookId;

    /**
     * CP书籍id
     */
    private String cpBookId;

    /**
     * 书籍类型：1、文本，2、有声读物
     */
    private Integer bookType;

    /**
     * CP合作伙伴ID
     */
    private Long cpPartnerId;

    /**
     * 书籍名称
     */
    private String bookName;

    /**
     * 我方章节ID
     */
    private Long chapterId;

    /**
     * CP章节ID
     */
    private String cpChapterId;

    /**
     * 章节名称
     */
    private String chapterName;

    /**
     * 问题类型，1：CP章节ID冲突，2：章节内容为空，3：接口调用异常
     */
    private Integer problemType;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 更新时间
     */
    private Date utime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCpBookId() {
        return cpBookId;
    }

    public void setCpBookId(String cpBookId) {
        this.cpBookId = cpBookId;
    }

    public Integer getBookType() {
        return bookType;
    }

    public void setBookType(Integer bookType) {
        this.bookType = bookType;
    }

    public Long getCpPartnerId() {
        return cpPartnerId;
    }

    public void setCpPartnerId(Long cpPartnerId) {
        this.cpPartnerId = cpPartnerId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getCpChapterId() {
        return cpChapterId;
    }

    public void setCpChapterId(String cpChapterId) {
        this.cpChapterId = cpChapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Integer getProblemType() {
        return problemType;
    }

    public void setProblemType(Integer problemType) {
        this.problemType = problemType;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static ExceptionBookRecordVO build(Book book, Long chapterId, String cpChapterId, String chapterName, int exceptionType, String extend) {
        ExceptionBookRecordVO exceptionBookRecordVO = new ExceptionBookRecordVO();

        exceptionBookRecordVO.setBookId(book.getBookId());
        exceptionBookRecordVO.setBookName(book.getName());
        exceptionBookRecordVO.setBookType(book.getBookType());
        exceptionBookRecordVO.setCpPartnerId(Long.parseLong(book.getPartnerId()));
        exceptionBookRecordVO.setCpBookId(book.getCpBookId());
        exceptionBookRecordVO.setChapterId(chapterId);
        exceptionBookRecordVO.setCpChapterId(cpChapterId);
        exceptionBookRecordVO.setChapterName(chapterName);
        exceptionBookRecordVO.setProblemType(exceptionType);
        exceptionBookRecordVO.setExtend(extend);

        return exceptionBookRecordVO;
    }

}
