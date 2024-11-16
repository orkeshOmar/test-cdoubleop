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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz
 * @create 2017-08-28 下午5:41
 */
@Service
public class YueTingReadServiceImpl implements ClientService {

    private static final String SUCCESS_CODE = "200";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONObject object = sentGet(owchPartner.getBookListUrl());

        JSONArray books = object.getJSONArray("date");
        if (books == null || books.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("articleid"));
            cpBook.setName(book.getString("name"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject object = sentGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId));

        JSONObject data = object.getJSONObject("date");
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("articleid"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(data.getString("fullflag"));
        cpBook.setCategory(data.getString("Sortid"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONObject object = sentGet(MessageFormat.format(owchPartner.getChapterListUrl(), bookId));
        JSONArray chapters = object.getJSONArray("date");
        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());

        for (int j = 0; j < chapters.size(); j++) {
            JSONObject chapter = chapters.getJSONObject(j);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterId"));
            cpChapter.setName(chapter.getString("chaptername"));
            cpChapters.add(cpChapter);
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String response = HttpUtil.sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId));
        JSONObject data = JSONObject.parseObject(response);

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
        return new ThirdPart[]{ThirdPart.YUE_TING};
    }

    private JSONObject sentGet(String url) throws Exception{
        String response = HttpUtil.sendGet(url);
        JSONObject object = JSONObject.parseObject(response);
        String code = object.getString("code");
        if (!SUCCESS_CODE.equals(object.getString("code"))) {
            throw new Exception("[阅庭书院]接口数据获取异常，error code : " + code +" , error msg : " + object.getString("error"));
        }
        return object;
    }

}
