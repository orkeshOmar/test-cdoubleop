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
 * @description: 有乐文学网
 * @author: songwj
 * @date: 2019-08-21 15:51
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class YouLeWenXueWangServiceImpl implements ClientService {

    private static final String SUCCESS_CODE = "200";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId()), JSONArray.class, owchPartner.getName());
        return (List<CPBook>) CollectionUtils.collect(books, o -> {
            JSONObject book = (JSONObject) o;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookid"));
            cpBook.setName(book.getString("bookname"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId), JSONObject.class, owchPartner.getName());
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookid"));
        cpBook.setName(data.getString("bookname"));
        cpBook.setAuthor(data.getString("pen_name"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(StringUtils.equals("1", data.getString("state")) ? "1" : "0");// 完结状态，1：已完结，2：连载中

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONObject chapterList = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId), JSONObject.class, owchPartner.getName());
        JSONArray volumes = chapterList.getJSONArray("volumes");

        List<CPVolume> cpVolumes = new ArrayList<>(1);

        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);

            List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(volume.getJSONArray("chapters"), o -> {
                JSONObject chapter = (JSONObject) o;
                return new CPChapter(chapter.getString("chapter_id"), chapter.getString("chapter_name"));
            });

            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(volume.getString("volume_id"));
            cpVolume.setName(volume.getString("volume_name"));
            cpVolume.setChapterList(collect);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId), JSONObject.class, owchPartner.getName());
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(StringUtils.replace(data.getString("content"), "<p>", "")
                .replace("</p>", "\n")
                .replace("<strong style=\"background-color: rgb(255, 255, 204); color: rgb(255, 0, 0);\">", "")
                .replace("<span style=\"background-color: rgb(206, 218, 194);\">", "")
                .replace("</span>", ""));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YOU_LE_ZHONG_WEN_WANG};
    }

    private <T> T doGet(String url, Class<T> tClass, String cpName) throws Exception {
        String json = HttpUtil.sendGet(url);

        if (StringUtils.isBlank(json)) {
            throw new Exception("[" + cpName + "]返回json串为空");
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!StringUtils.equals(jsonObject.getString("code"), SUCCESS_CODE)) {
            throw new Exception("[" + cpName + "]接口状态码异常,code=" + jsonObject.getString("code") + ",message=" + jsonObject.getString("message"));
        }

        return jsonObject.getObject("data", tClass);
    }

}
