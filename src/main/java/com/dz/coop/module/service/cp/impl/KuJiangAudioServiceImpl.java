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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @project: coop-client
 * @description: 酷匠有声读物对接（南京萌鹿）
 * @author: songwj
 * @date: 2019-07-25 19:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class KuJiangAudioServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(KuJiangAudioServiceImpl.class);

    private static final int SUCCESS_CODE = 100;

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getBookListUrl(), appId), JSONArray.class);
        if (data == null || data.size() < 0) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject book = data.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("book_id"));
            cpBook.setName(book.getString("book_name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), appId, bookId), JSONObject.class);
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("book_id"));
        cpBook.setName(data.getString("book_name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("introduce"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setTag(data.getString("tags"));
        cpBook.setCategory(data.getString("category"));
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("complete_status"), "N") ? "0" : "1");
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), appId, bookId), JSONArray.class);
        if (data == null || data.size() < 0) {
            return Collections.emptyList();
        }

        List<CPChapter> cpChapters = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapter_id"));
            cpChapter.setName(chapter.getString("chapter_name"));
            cpChapter.setContent(chapter.getString("voice_url"));
            cpChapters.add(cpChapter);
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), appId, cpBookId), JSONArray.class);
        CPChapter cpChapter = new CPChapter();

        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);

            if (StringUtils.isNotBlank(chapterId) && StringUtils.equals(chapterId, chapter.getString("chapter_id"))) {
                cpChapter.setContent(chapter.getString("voice_url"));
                break;
            }
        }

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.NAN_JING_MENG_LU};
    }

    private <T> T doGet(String url, Class<T> clazz) throws Exception {
        JSONObject json = JSONObject.parseObject(HttpUtil.sendGet(url));
        Integer code = json.getInteger("code");
        if (code != SUCCESS_CODE) {
            throw new Exception("接口获取失败，code=" + code + ",msg=" + json.getString("msg"));
        }
        return json.getObject("data", clazz);
    }

}
