package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz
 * @create 2017-07-21 上午10:22
 */
@Service
public class WeiYueServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, owchPartner.getApiKey());
        JSONObject resp = sentGet(bookListUrl);
        JSONArray result = resp.getJSONArray("result");
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            JSONObject book = result.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getInteger("bookid").toString());
            cpBook.setName(book.getString("bookname"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, owchPartner.getApiKey(), bookId);
        JSONObject resp = sentGet(bookInfoUrl);
        JSONObject book = resp.getJSONObject("result");
        CPBook cpBook = new CPBook();
        cpBook.setId(book.getInteger("bookid").toString());
        cpBook.setName(book.getString("bookname"));
        cpBook.setAuthor(book.getString("authorname"));
        cpBook.setBrief(book.getString("intro"));
        cpBook.setCover(book.getString("bookpic"));
        cpBook.setCategory(book.getString("category"));// 所属分类
        cpBook.setCompleteStatus(book.getString("fullflag"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, owchPartner.getApiKey(), bookId);
        JSONObject resp = sentGet(chapterListUrl);
        JSONArray array = resp.getJSONArray("result");
        if (array.size() == 0) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject chapter = array.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getInteger("chapterid").toString());
            cpChapter.setName(chapter.getString("chaptername"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterInfoUrl = owchPartner.getChapterInfoUrl();
        chapterInfoUrl = MessageFormat.format(chapterInfoUrl, owchPartner.getApiKey(), cpBookId, chapterId);
        JSONObject resp = sentGet(chapterInfoUrl);
        JSONObject result = resp.getJSONObject("result");
        String content = result.getString("content");
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(content);
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YUN_QUE};
    }

    private JSONObject sentGet(String url) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new Exception("url不能为空");
        }
        String resp = HttpUtil.sendGet(url);
        JSONObject respJson = JSONObject.parseObject(resp);
        Integer code = respJson.getInteger("code");
        if (8200 != code) {
            throw new Exception("通信异常,resoCode=" + code + ",msg=" + respJson.getString("message"));
        }
        return respJson;
    }
}
