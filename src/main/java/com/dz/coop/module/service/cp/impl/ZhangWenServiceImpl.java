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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @project: coop-client
 * @description: 掌文（黑岩）
 * @author: songwj
 * @date: 2019-03-25 19:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class ZhangWenServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONObject jsonObject = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId()));
        JSONArray books = (JSONArray) jsonObject.get("booklist");

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
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId));
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookid"));
        cpBook.setName(data.getString("bookname"));
        cpBook.setAuthor(data.getString("authorname"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("bookpic"));
        cpBook.setCompleteStatus(data.getString("fullflag"));// 1：已完结；0：连载中

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId));
        JSONArray vols = (JSONArray) data.get("vol");

        if (vols == null || vols.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> volumes = new ArrayList<>(vols.size());

        for (int i = 0; i < vols.size(); i++) {
            CPVolume cpVolume = new CPVolume();
            JSONObject volObject = vols.getJSONObject(i);
            cpVolume.setId(volObject.getString("volumeorder"));
            cpVolume.setName(volObject.getString("volumename"));
            JSONArray chapters = volObject.getJSONArray("chapter");
            List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
            CPChapter cpChapter = null;

            for (int j = 0; j < chapters.size(); j++) {
                cpChapter = new CPChapter();
                JSONObject chapterJsonObj = chapters.getJSONObject(j);
                cpChapter.setId(chapterJsonObj.getString("chapterid"));
                cpChapter.setName(chapterJsonObj.getString("chaptername"));
                cpChapters.add(cpChapter);
            }

            cpVolume.setChapterList(cpChapters);
            volumes.add(cpVolume);
        }

        return volumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String verify = "zhangwen";// 随机字符串，此处直接写为固定值
        String str1 = DigestUtils.md5Hex(chapterId + "#" + owchPartner.getAliasId() + "#" + owchPartner.getApiKey());
        String sign = DigestUtils.md5Hex(verify + "#" + str1);
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), chapterId, verify, sign));
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
        return new ThirdPart[]{ThirdPart.ZHANG_WEN};
    }

    private JSONObject doGet(String url) throws Exception {
        String json = HttpUtil.sendGet(url);

        if (StringUtils.isBlank(json)) {
            throw new Exception("【掌文】返回json串为空");
        }

        return JSONObject.parseObject(json);
    }

}
