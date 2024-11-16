package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.EscapeUtil;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.Constant;
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
 * @description: 青草文化接口
 * @author: songwj
 * @date: 2020-02-10 18:45
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class QingCaoWenHuaServiceImpl implements ClientService {

    Logger logger = LoggerFactory.getLogger(QingCaoWenHuaServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String bookListUrl = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey());
        JSONArray books = HttpUtil.doGet(bookListUrl, JSONArray.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_1);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookid"));
            String bookName = book.getString("name");
            cpBook.setName(bookName.contains("[授权]") ? bookName.substring(0, bookName.indexOf("[授权]")) : bookName);
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookListUrl = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId);
        JSONObject data = HttpUtil.doGet(bookListUrl, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_1);

        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookid"));
        String bookName = data.getString("name");
        cpBook.setName(bookName.contains("[授权]") ? bookName.substring(0, bookName.indexOf("[授权]")) : bookName);
        String author = data.getString("author");
        cpBook.setAuthor(StringUtils.isNotBlank(author) ? author : "佚名");
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getString("thumb"));
        // 1连载中；0已完本
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("serial"), "1") ? "0" : "1");
        cpBook.setCategory(data.getString("catid"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId) + "&pagesize=10000";
        JSONArray chapterArr = HttpUtil.doGet(chapterListUrl, JSONArray.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_1);

        List<CPChapter> cpChapters = (List<CPChapter>) CollectionUtils.collect(chapterArr, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(chapter.getString("chapterid"), chapter.getString("name"));
        });

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
        String chapterInfoUrl = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), chapterId);
        JSONObject chapterObj = HttpUtil.doGet(chapterInfoUrl, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_1);
        CPChapter cpChapter = new CPChapter();

        cpChapter.setContent(EscapeUtil.escape(chapterObj.getString("content").replaceAll("<spanyes';font-family:宋体;mso-ascii-font-family:calibri;mso-hansi-font-family:calibri;mso-bidi-font-family:'times=\"\"new=\"\"roman';font-size:10.5000pt;=\"\"mso-font-kerning:1.0000pt;\"=\"\">", "").replaceAll("</spanyes';font-family:宋体;mso-ascii-font-family:calibri;>", "").replaceAll("<spanyes';font-family:calibri;mso-fareast-font-family:宋体;mso-bidi-font-family:'times=\"\"new=\"\"roman';font-size:10.5000pt;mso-font-kerning:1.0000pt;\"=\"\">", "").replaceAll("</spanyes';font-family:calibri;mso-fareast-font-family:宋体;>", "")));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QING_CAO_WEN_HUA};
    }

}
