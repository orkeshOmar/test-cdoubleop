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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-05-25 下午1:32
 */
@Service
public class ZhangYueServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String clientId = owchPartner.getAliasId();
        JSONArray books = doPost(MessageFormat.format(owchPartner.getBookListUrl(), clientId, sign(clientId, owchPartner.getApiKey())), JSONArray.class);
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookId"));
            cpBook.setName(book.getString("name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String clientId = owchPartner.getAliasId();
        JSONObject data = doPost(MessageFormat.format(owchPartner.getBookInfoUrl(), clientId, sign(clientId, owchPartner.getApiKey(), bookId), bookId), JSONObject.class);
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("brief"));
        cpBook.setCover(data.getString("cover"));
        String bookStatus = data.getString("completeStatus");
        cpBook.setCompleteStatus(StringUtils.equals("Y", bookStatus) ? "1" : "0");
        String categoryId = data.getString("categoryId");
        cpBook.setCategory(StringUtils.isNotBlank(categoryId) ? categoryId.split(",")[0] : "");
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String clientId = owchPartner.getAliasId();
        JSONArray chapters = doPost(MessageFormat.format(owchPartner.getChapterListUrl(), clientId, sign(clientId, owchPartner.getApiKey(), bookId), bookId), JSONArray.class);
        if (chapters == null || chapters.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
        for (int i = 0; i < chapters.size(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterId"));
            cpChapter.setName(chapter.getString("title"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String clientId = owchPartner.getAliasId();
        JSONObject chapter = doPost(MessageFormat.format(owchPartner.getChapterInfoUrl(), clientId, sign(clientId, owchPartner.getApiKey(), cpBookId, chapterId), cpBookId, chapterId), JSONObject.class);
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
        return new ThirdPart[]{ThirdPart.ZHNAG_YUE, ThirdPart.BEI_JING_ZHANG_YUE};
    }

    public <T> T doPost(String url, Class<T> clazz) {
        String resp = HttpUtil.sendGet(url);
        return JSONObject.parseObject(resp, clazz);
    }

    private static String sign(String... paras) {
        StringBuilder sb = new StringBuilder();
        for (String param : paras) {
            sb.append(param);
        }
        return DigestUtils.md5Hex(sb.toString()).toLowerCase();
    }

}
