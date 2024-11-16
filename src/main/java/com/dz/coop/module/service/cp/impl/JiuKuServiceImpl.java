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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2017-12-26 上午9:18
 */
@Service
public class JiuKuServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String pid = owchPartner.getAliasId();
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), pid, createSign(pid, owchPartner.getApiKey())), JSONArray.class);
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPBook> list = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject object = books.getJSONObject(i);
            CPBook cpBook = new CPBook(object.getString("id"), object.getString("booktitle"));
            list.add(cpBook);
        }

        return list;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String pid = owchPartner.getAliasId();
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), pid, createSign(pid, owchPartner.getApiKey()), bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("title"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("summary"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(data.getString("isFull"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String pid = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), pid, createSign(pid, owchPartner.getApiKey()), bookId), JSONArray.class);
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> list = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter(chapter.getString("id"), chapter.getString("title"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        list.add(cpVolume);
        return list;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String pid = owchPartner.getAliasId();
        String content = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), pid, createSign(pid, owchPartner.getApiKey()), cpBookId, chapterId), String.class);

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
        return new ThirdPart[]{ThirdPart.JIU_KU};
    }

    private <T> T doGet(String url, Class<T> clazz) throws Exception {
        JSONObject json = JSONObject.parseObject(HttpUtil.sendGet(url));
        Integer status = json.getInteger("status");
        if (!ObjectUtils.equals(1, status)) {
            throw new Exception("接口获取失败，status=" + status + ",msg=" + json.getString("data"));
        }
        return json.getObject("data", clazz);
    }

    private String createSign(String pid, String key) {
        return DigestUtils.md5Hex("pid=" + pid + "&key=" + key);
    }
}
