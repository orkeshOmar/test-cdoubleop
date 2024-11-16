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
 * @description: 笔尚
 * @author: songwj
 * @date: 2020-06-23 20:48
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BiShangServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey());
        JSONArray books = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookid"));
            cpBook.setName(book.getString("bookname"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookid"));
        cpBook.setName(data.getString("bookname"));
        cpBook.setAuthor(data.getString("authorname"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("bookpic"));
        // 完结状态：1 完结，0 连载
        cpBook.setCompleteStatus(data.getString("fullflag"));
        cpBook.setCategory(data.getString("category"));
        cpBook.setTag(data.getString("tag"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId);
        JSONArray chapters = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);

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
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
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
        return new ThirdPart[]{ThirdPart.BI_SHANG};
    }

}
