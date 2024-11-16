package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz
 * @create 2017-06-01 下午1:53
 */
@Service
public class YouYueServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(YouYueServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String response = HttpUtil.sendGet(owchPartner.getBookListUrl());
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("error message" + json.getString("error"));
        }
        JSONArray array = json.getJSONArray("data");
        if (array == null || array.size() == 0) {
            throw new Exception("books size is 0");
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            String bookId = item.getString("articleid");
            String bookName = item.getString("articlename");
            CPBook cpBook = new CPBook();
            cpBook.setId(bookId);
            cpBook.setName(bookName);
            cpBooks.add(cpBook);
        }
        return cpBooks;


    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), bookId);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("error message : " + json.getString("error"));
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new Exception("bookinfo is null");
        }
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("articleid"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCategory(data.getString("Sortid"));// 所属分类
        String bookStatus = data.getString("fullflag");
        if ("1".equals(bookStatus)) { //完结
            cpBook.setCompleteStatus("1");
        } else {
            cpBook.setCompleteStatus("0");
        }
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), bookId);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("error message" + json.getString("error"));
        }
        JSONArray data = json.getJSONArray("data");
        if (data == null || data.size() == 0) {
            throw new Exception("chapters is null");
        }
        List<CPVolume> columeList = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterid"));
            cpChapter.setName(chapter.getString("chaptername"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        columeList.add(cpVolume);
        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("error message" + json.getString("error"));
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new Exception("chapterinfo is null");
        }
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YOU_YUE};
    }
}
