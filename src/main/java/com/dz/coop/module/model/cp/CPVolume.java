package com.dz.coop.module.model.cp;

import java.util.ArrayList;
import java.util.List;

public class CPVolume {


    private String id;
    private String name;
    private String bookId;

    List<CPChapter> chapterList = new ArrayList<>();

    List<CPChapter> chapterlist = new ArrayList<>();

    public CPVolume(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CPVolume() {
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

    public List<CPChapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<CPChapter> chapterList) {
        this.chapterList = chapterList;
    }

    public void add(CPChapter cpChapter) {
        chapterList.add(cpChapter);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<CPChapter> getChapterlist() {
        return chapterlist;
    }

    public void setChapterlist(List<CPChapter> chapterlist) {
        this.chapterlist = chapterlist;
        this.chapterList = chapterlist;
    }
}
