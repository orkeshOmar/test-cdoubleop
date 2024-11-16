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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 万层中文网接口对接
 * @author: songwj
 * @date: 2022-01-04 8:39 下午
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2022 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class WanCengZhongWenWangServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId());
        JSONArray books = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.ERROR, Constant.DATA, Constant.SUCCESS_CODE_200);

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
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.ERROR, Constant.DATA, Constant.SUCCESS_CODE_200);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("articleid"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        // 是否全本，1-全本， 0-连载
        cpBook.setCompleteStatus(data.getString("fullflag"));
        cpBook.setCategory(data.getString("sort"));
        cpBook.setTag(data.getString("keywords"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId);
        JSONArray chapters = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.ERROR, Constant.DATA, Constant.SUCCESS_CODE_200);

        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(chapter.getString("chapterid"), chapter.getString("chaptername"));
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
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.ERROR, Constant.DATA, Constant.SUCCESS_CODE_200);
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
        return new ThirdPart[]{ThirdPart.WAN_CENG_ZHONG_WEN_WANG};
    }

}