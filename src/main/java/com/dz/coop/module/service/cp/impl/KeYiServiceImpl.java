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
 * @description: 苛苡接口对接
 * @author: songwj
 * @date: 2020-10-26 10:55
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class KeYiServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId());
        JSONArray books = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("name"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        // 完结状态：1 完结，0 连载
        cpBook.setCompleteStatus(data.getBoolean("finished") ? "1" : "0");
        cpBook.setCategory(data.getString("minorCate"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId);
        JSONArray volumes = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);
        List<CPVolume> cpVolumes = new ArrayList<>(1);

        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);
            JSONArray chapters = volume.getJSONArray("chapters");

            List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
                JSONObject chapter = (JSONObject) obj;
                return new CPChapter(chapter.getString("cid"), chapter.getString("name"));
            });

            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(volume.getString("id"));
            cpVolume.setName(volume.getString("name"));
            cpVolume.setChapterList(collect);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);
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
        return new ThirdPart[]{ThirdPart.KE_YI};
    }

}
