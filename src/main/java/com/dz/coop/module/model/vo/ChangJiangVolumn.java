package com.dz.coop.module.model.vo;

import java.util.List;

/**
 * @author panqz
 * @create 2017-03-24 下午4:02
 */

public class ChangJiangVolumn {

    private String volumnName;
    private List<ChangjiangChapter> changjiangChapters;

    public String getVolumnName() {
        return volumnName;
    }

    public void setVolumnName(String volumnName) {
        this.volumnName = volumnName;
    }

    public List<ChangjiangChapter> getChangjiangChapters() {
        return changjiangChapters;
    }

    public void setChangjiangChapters(List<ChangjiangChapter> changjiangChapters) {
        this.changjiangChapters = changjiangChapters;
    }
}
