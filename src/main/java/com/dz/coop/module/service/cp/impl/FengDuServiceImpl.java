package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @project: coop-client
 * @description: 疯读接口对接
 * @author: songwj
 * @date: 2021-02-05 10:42
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class FengDuServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String aliasId = owchPartner.getAliasId();
        String apiKey = owchPartner.getApiKey();

        Map<String, Object> map = new TreeMap<>();
        map.put("ts", ts);
        map.put("partner_id", aliasId);
        String sign = getSign(map, apiKey);

        String url = MessageFormat.format(owchPartner.getBookListUrl(), aliasId, ts, sign);
        JSONObject bookList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.RESULT_CODE, Constant.ERR_MSG, Constant.RESULT, Constant.SUCCESS_CODE_2000);
        JSONArray books = bookList.getJSONArray(Constant.BOOKLIST);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("title"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String cpBookId) throws Exception {
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String aliasId = owchPartner.getAliasId();
        String apiKey = owchPartner.getApiKey();

        Map<String, Object> map = new TreeMap<>();
        map.put("ts", ts);
        map.put("partner_id", aliasId);
        map.put("book_id", cpBookId);
        String sign = getSign(map, apiKey);

        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), aliasId, ts, sign, cpBookId);
        JSONObject book = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.RESULT_CODE, Constant.ERR_MSG, Constant.RESULT, Constant.SUCCESS_CODE_2000);
        JSONObject data = book.getJSONObject(Constant.BOOK);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("title"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("desc"));
        cpBook.setCover(data.getString("cover"));
        // 书籍连载状态，0：未完结（连载）1：已完结
        cpBook.setCompleteStatus(data.getString("is_finished"));
        // 二级分类ID
        cpBook.setCategory(data.getString("category_id"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String cpBookId) throws Exception {
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String aliasId = owchPartner.getAliasId();
        String apiKey = owchPartner.getApiKey();

        Map<String, Object> map = new TreeMap<>();
        map.put("ts", ts);
        map.put("partner_id", aliasId);
        map.put("book_id", cpBookId);
        String sign = getSign(map, apiKey);

        String url = MessageFormat.format(owchPartner.getChapterListUrl(), aliasId, sign, ts, cpBookId);
        JSONObject chapterList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.RESULT_CODE, Constant.ERR_MSG, Constant.RESULT, Constant.SUCCESS_CODE_2000);
        JSONArray chapters = chapterList.getJSONArray(Constant.CHAPTERS);

        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(chapter.getString("id"), chapter.getString("title"));
        });

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(collect);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpChapterId) throws Exception {
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String aliasId = owchPartner.getAliasId();
        String apiKey = owchPartner.getApiKey();

        Map<String, Object> map = new TreeMap<>();
        map.put("ts", ts);
        map.put("partner_id", aliasId);
        map.put("book_id", cpBookId);
        map.put("chapter_id", cpChapterId);
        String sign = getSign(map, apiKey);

        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), aliasId, sign, ts, cpBookId, cpChapterId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.RESULT_CODE, Constant.ERR_MSG, Constant.RESULT, Constant.SUCCESS_CODE_2000);
        JSONObject chapter = data.getJSONObject(Constant.CHAPTER);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(chapter.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpVolumeId, String cpChapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, cpChapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.FENG_DU};
    }

    private String getSign(Map<String, Object> map, String apiKey) {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> me : entrySet) {
            sb.append(me.getValue()).append("|");
        }

        return DigestUtils.md5Hex(sb.toString() + apiKey);
    }

}
