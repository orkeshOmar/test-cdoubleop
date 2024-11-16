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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop_client
 * @description: 有弧文化书籍抓取接口
 * @author: songwj
 * @date: 2018-11-19 19:45
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2018 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class YouHuServiceImpl implements ClientService {


    private static final String SUCCESS_CODE = "0";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey()), JSONArray.class);
        return (List<CPBook>) CollectionUtils.collect(books, o -> {
            JSONObject book = (JSONObject) o;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookId"));
            cpBook.setName(book.getString("bookName"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), JSONObject.class);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("bookAuthor"));
        cpBook.setBrief(data.getString("bookIntroduction"));
        cpBook.setCover(data.getString("bookCover"));
        // 是否完结，1：完结，0：连载中
        cpBook.setCompleteStatus(data.getString("isFinished"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray chapters = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), JSONArray.class);
        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, new Transformer<Object, CPChapter>() {
            @Override
            public CPChapter transform(Object o) {
                JSONObject chapter = (JSONObject) o;
                String chapterName = chapter.getString("chapterName");
                return new CPChapter(chapter.getString("chapterId"), "第" + chapter.getString("chapterNum") + "章 " + (StringUtils.isNotBlank(chapterName) ? chapterName.trim() : chapterName));
            }
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
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), cpBookId, chapterId), JSONObject.class);
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
        return new ThirdPart[]{ThirdPart.YOU_HU_WEN_HUA};
    }

    private <T> T doGet(String url, Class<T> tClass) throws Exception {
        String json = HttpUtil.sendGet(url);
        if (StringUtils.isBlank(json)) {
            throw new Exception("有弧文化返回json串为空");
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!StringUtils.equals(jsonObject.getString("code"), SUCCESS_CODE)) {
            throw new Exception("有弧文化接口状态码异常,code=" + jsonObject.getString("code") + ",message=" + jsonObject.getString("message"));
        }
        return jsonObject.getObject("result", tClass);
    }

}
