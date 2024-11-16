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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 原创书殿接口对接
 * @author: songwj
 * @date: 2020-02-05 19:34
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class YuanChuanShuDianServiceImpl implements ClientService {

    private static final String SUCCESS_CODE = "0";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId()), JSONArray.class);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("booktitle"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId), JSONObject.class);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("booktitle"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("Introduction"));
        cpBook.setCover(data.getString("cover"));
        // 连载状态 （0：连载中 1：完结）
        cpBook.setCompleteStatus(data.getString("state"));
        cpBook.setCategory(data.getString("nclass"));
        cpBook.setTag(data.getString("Labels"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray chapters = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId), JSONArray.class);
        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(chapter.getString("id"), chapter.getString("Title"));
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
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId), JSONObject.class);
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
        return new ThirdPart[]{ThirdPart.YUAN_CHUAN_SHU_DIAN};
    }

    private <T> T doGet(String url, Class<T> tClass) throws Exception {
        String json = HttpUtil.sendGet(url);
        if (StringUtils.isBlank(json)) {
            throw new Exception("[原创书殿]返回json串为空");
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!StringUtils.equals(jsonObject.getString("code"), SUCCESS_CODE)) {
            throw new Exception("[原创书殿]接口状态码异常,code=" + jsonObject.getString("code") + ",message=" + jsonObject.getString("msg"));
        }

        return jsonObject.getObject("data", tClass);
    }

}
