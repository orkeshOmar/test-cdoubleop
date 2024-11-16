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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 九创文学接口
 * @author: songwj
 * @date: 2020-05-20 20:17
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class JiuChuangWenXueServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = JSON.parseArray(doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey())));

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("novelId"));
            cpBook.setName(book.getString("novelName"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = JSON.parseObject(doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId, owchPartner.getApiKey())));

        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("novelId"));
        cpBook.setName(data.getString("novelName"));
        cpBook.setAuthor(data.getString("novelAuthor"));
        cpBook.setBrief(data.getString("novelInfo"));
        cpBook.setCover(data.getString("novelCover"));
        // true为连载
        cpBook.setCompleteStatus(data.getBoolean("novelProcess") ? "0" : "1");
        cpBook.setCategory(data.getString("typeId"));
        cpBook.setTag(data.getString("novelTags"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray chapters = JSON.parseArray(doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId, owchPartner.getApiKey())));

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
        JSONObject data = JSON.parseObject(doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId, owchPartner.getApiKey())));

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("chapterContent"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JIU_CHUANG_WEN_XUE};
    }

    private String doGet(String url) throws Exception {
        String resp = HttpUtil.sendGet(url);

        if (StringUtils.isBlank(resp)) {
            throw new Exception("[九创文学]返回的json为空");
        }

        return resp;
    }

}
