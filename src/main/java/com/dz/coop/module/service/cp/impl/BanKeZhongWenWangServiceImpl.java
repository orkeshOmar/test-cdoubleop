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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 半刻中文网接口
 * @author: songwj
 * @date: 2020-02-13 14:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BanKeZhongWenWangServiceImpl implements ClientService {

    Logger logger = LoggerFactory.getLogger(BanKeZhongWenWangServiceImpl.class);

    private static final int SUCCESS_CODE = 200;

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String bookListUrl = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId());
        JSONArray books = doGet(bookListUrl, owchPartner.getName(), JSONArray.class);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("articleid"));
            cpBook.setName(book.getString("articlename"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookListUrl = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId);
        JSONObject data = doGet(bookListUrl, owchPartner.getName(), JSONObject.class);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("articleid"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        // 是否全本，1-全本， 0-连载
        cpBook.setCompleteStatus(data.getString("fullflag"));
        cpBook.setCategory(data.getString("sort"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId);
        JSONArray chapterArr = doGet(chapterListUrl, owchPartner.getName(), JSONArray.class);

        List<CPChapter> cpChapters = new ArrayList<>();

        CPChapter cpChapter = null;

        for (int i = 0, len = chapterArr.size(); i < len; i++) {
            JSONObject chapterObj = chapterArr.getJSONObject(i);
            // 只获取章节，过滤卷
            if (chapterObj.getInteger("chaptertype") == 0) {
                cpChapter = new CPChapter();
                cpChapter.setId(chapterObj.getString("chapterid"));
                cpChapter.setName(chapterObj.getString("chaptername"));
                cpChapters.add(cpChapter);
            }
        }

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterInfoUrl = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId);
        JSONObject chapterObj = doGet(chapterInfoUrl, owchPartner.getName(), JSONObject.class);
        CPChapter cpChapter = new CPChapter();

        cpChapter.setContent(chapterObj.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.BAN_KE_ZHONG_WEN_WANG};
    }

    private <T> T doGet(String url, String cpName, Class<T> tClass) throws Exception {
        String json = HttpUtil.sendGet(url);
        if (StringUtils.isBlank(json)) {
            throw new Exception("[" + cpName + "]返回json串为空");
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getInteger("code") != SUCCESS_CODE) {
            throw new Exception("[" + cpName + "]接口状态码异常,code=" + jsonObject.getString("code") + ",message=" + jsonObject.getString("error"));
        }

        return jsonObject.getObject("data", tClass);
    }

}
