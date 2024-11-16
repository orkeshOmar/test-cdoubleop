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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * @project: coop-client
 * @description: 纵横中文网
 * @author: songwj
 * @date: 2020-06-23 20:48
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class ZhongHengZhongWenWangServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String aliasId = owchPartner.getAliasId();

        TreeMap<String, String> params = new TreeMap<>();
        params.put("method", "api.books");
        params.put("t", timestamp);
        params.put("api_key", aliasId);

        String sign = getSign(params, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getBookListUrl(), timestamp, aliasId, sign);
        JSONArray books = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_200);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            String bookId = String.valueOf(obj);
            CPBook cpBook = new CPBook();
            cpBook.setId(bookId);
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String aliasId = owchPartner.getAliasId();

        TreeMap<String, String> params = new TreeMap<>();
        params.put("method", "book");
        params.put("book_id", bookId);
        params.put("t", timestamp);
        params.put("api_key", aliasId);

        String sign = getSign(params, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, timestamp, aliasId, sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_200);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("authorName"));
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getString("coverUrl"));
        // 完结状态：1 完结，0 连载
        cpBook.setCompleteStatus(data.getString("serialStatus"));
        cpBook.setCategory(data.getString("categoryId"));
        cpBook.setTag(data.getString("keywords"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String aliasId = owchPartner.getAliasId();

        TreeMap<String, String> params = new TreeMap<>();
        params.put("method", "chapter.dir");
        params.put("book_id", bookId);
        params.put("t", timestamp);
        params.put("api_key", aliasId);

        String sign = getSign(params, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), bookId, timestamp, aliasId, sign);
        JSONArray chapters = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_200);

        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(chapter.getString("chapterId"), chapter.getString("chapterName"));
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
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String aliasId = owchPartner.getAliasId();

        TreeMap<String, String> params = new TreeMap<>();
        params.put("method", "chapter");
        params.put("book_id", cpBookId);
        params.put("chapter_id", chapterId);
        params.put("t", timestamp);
        params.put("api_key", aliasId);

        String sign = getSign(params, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, timestamp, aliasId, sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_200);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHONG_HENG_ZHONG_WEN_WANG};
    }

    private String getSign(TreeMap<String, String> params, String secret) {
        StringBuilder sb = new StringBuilder(secret);
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            sb.append(name).append(params.get(name));
        }
        sb.append(secret);
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

}
