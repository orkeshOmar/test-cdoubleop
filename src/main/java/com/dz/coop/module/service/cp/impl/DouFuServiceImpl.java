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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-07-09 上午11:22
 */
@Service
public class DouFuServiceImpl implements ClientService {

    private static final String SUCCESS_CODE = "0";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray objects = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(),
                sign(owchPartner.getAliasId(), owchPartner.getApiKey())), JSONArray.class);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            JSONObject object = objects.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(object.getString("id"));
            cpBook.setName(object.getString("name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject object = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(),
                sign(owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), bookId), JSONObject.class);
        CPBook cpBook = new CPBook();
        cpBook.setId(object.getString("id"));
        cpBook.setName(object.getString("name"));
        cpBook.setAuthor(object.getString("author"));
        cpBook.setBrief(object.getString("brief"));
        cpBook.setCover(object.getString("cover"));
        cpBook.setCompleteStatus(object.getString("end_state"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray objects = sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(),
                sign(owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), bookId), JSONArray.class);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            JSONObject chapter = objects.getJSONObject(i);
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
        JSONObject object = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(),
                sign(owchPartner.getAliasId(), owchPartner.getApiKey(), cpBookId, chapterId), cpBookId, chapterId), JSONObject.class);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(object.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{
                ThirdPart.DOU_FU
        };
    }

    private <T> T sendGet(String url, Class<T> clazz) {
        String resp = HttpUtil.sendGet(url);
        JSONObject object = JSON.parseObject(resp);
        String code = object.getString("code");
        if (!StringUtils.equals(SUCCESS_CODE, code)) {
            throw new RuntimeException("豆腐接口异常,code=" + code + ",msg=" + object.getString("msg"));
        }
        return object.getObject("data", clazz);
    }

    private String sign(String... paras) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paras.length; i++) {
            sb.append(paras[i]);
        }
        return DigestUtils.md5Hex(sb.toString());
    }


}
