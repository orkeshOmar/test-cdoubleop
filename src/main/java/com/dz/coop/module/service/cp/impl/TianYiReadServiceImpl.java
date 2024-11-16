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
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2017-10-11 下午4:41
 */
@Service
public class TianYiReadServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        String clientId = owchPartner.getAliasId();
        JSONArray books = sentGet(owchPartner.getBookListUrl(), JSONArray.class, clientId, createSign(clientId, owchPartner.getApiKey()));
        if (books == null || books.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0, length = books.size(); i < length; i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String clientId = owchPartner.getAliasId();
        JSONObject book = sentGet(owchPartner.getBookInfoUrl(), JSONObject.class, clientId, createSign(clientId, owchPartner.getApiKey(), bookId), bookId);

        CPBook cpBook = new CPBook();
        cpBook.setId(book.getString("id"));
        cpBook.setName(book.getString("name"));
        cpBook.setAuthor(book.getString("author"));
        cpBook.setBrief(book.getString("brief"));
        cpBook.setCover(book.getString("cover"));
        cpBook.setCategory(book.getString("category"));
        cpBook.setCompleteStatus("Y".equals(book.getString("completeStatus")) ? "1" : "0");

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String clientId = owchPartner.getAliasId();
        JSONArray chapters = sentGet(owchPartner.getChapterListUrl(), JSONArray.class, clientId, createSign(clientId, owchPartner.getApiKey(), bookId), bookId);
        if (chapters == null || chapters.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
        for (int i = 0, length = chapters.size(); i < length; i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("id"));
            cpChapter.setName(chapter.getString("name"));
            cpChapters.add(cpChapter);
        }

        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        String clientId = owchPartner.getAliasId();
        JSONObject chapter = sentGet(owchPartner.getChapterInfoUrl(), JSONObject.class, clientId, createSign(clientId, owchPartner.getApiKey(), cpBookId, chapterId), cpBookId, chapterId);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(chapter.getString("content"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.TIAN_YI};
    }

    private <T> T sentGet(String url, Class<T> clazz, String... params) {
        String resp = HttpUtil.sendGet(MessageFormat.format(url, params));
        return JSONObject.parseObject(resp, clazz);
    }

    private String createSign(String clientId, String key, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(clientId).append(key);
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
        }
        return DigestUtils.md5Hex(sb.toString());
    }
}
