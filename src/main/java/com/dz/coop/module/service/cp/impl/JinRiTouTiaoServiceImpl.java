package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.org.apache.commons.codec.digest.DigestUtils;
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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 今日头条对接
 * @author: songwj
 * @date: 2020-05-28 11:39
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class JinRiTouTiaoServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Long ts = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(new StringBuilder(owchPartner.getAliasId()).append(owchPartner.getApiKey()).append(ts.toString()).toString());
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), ts.toString(), sign);
        JSONObject bookList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
        JSONArray books = bookList.getJSONArray("book_list");

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("book_id"));
            cpBook.setName(book.getString("book_name"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Long ts = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(new StringBuilder(owchPartner.getAliasId()).append(owchPartner.getApiKey()).append(ts.toString()).append(bookId).toString());
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), ts.toString(), bookId, sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("book_id"));
        cpBook.setName(data.getString("book_name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getString("thumb_url"));
        // 书籍连载状态，0：已完结，1：连载中
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("creation_status"), "1") ? "0" : "1");
        cpBook.setCategory(data.getString("category"));
        cpBook.setTag(data.getString("tags"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        Long ts = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(new StringBuilder(owchPartner.getAliasId()).append(owchPartner.getApiKey()).append(ts.toString()).append(bookId).toString());
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), ts.toString(), bookId, sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
        JSONArray chapters = data.getJSONArray("chapter_list");

        List<CPVolume> cpVolumes = new ArrayList<>();
        JSONObject chapterObj = chapters.getJSONObject(0);
        String volumeIdFlag = chapterObj.getString("volume_id");
        String volumeNameFlag = chapterObj.getString("volume_name");
        List<CPChapter> chapterList = new ArrayList<>();

        for (int n = 0, len = chapters.size(); n < len; n++) {
            JSONObject chapter = chapters.getJSONObject(n);
            String volumeId = chapter.getString("volume_id");
            String volumeName = chapter.getString("volume_name");

            // 卷ID相同归属于同一卷
            if (StringUtils.equals(volumeIdFlag, volumeId)) {
                CPChapter cpChapter = new CPChapter(chapter.getString("chapter_id"), chapter.getString("title"));
                chapterList.add(cpChapter);

                if (n == len - 1) {
                    addVolume(cpVolumes, volumeIdFlag, volumeNameFlag, chapterList);
                }
            } else {
                // 卷ID不同则开始分卷
                addVolume(cpVolumes, volumeIdFlag, volumeNameFlag, chapterList);

                chapterList = new ArrayList<>();
                volumeIdFlag = volumeId;
                volumeNameFlag = volumeName;

                CPChapter cpChapter = new CPChapter(chapter.getString("chapter_id"), chapter.getString("title"));
                chapterList.add(cpChapter);
            }
        }

        return cpVolumes;
    }

    private void addVolume(List<CPVolume> cpVolumes, String volumeId, String volumeName, List<CPChapter> chapterList) {
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId(volumeId);
        cpVolume.setName(volumeName);
        cpVolume.setChapterList(chapterList);
        cpVolumes.add(cpVolume);
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        Long ts = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(new StringBuilder(owchPartner.getAliasId()).append(owchPartner.getApiKey()).append(ts.toString()).append(chapterId).toString());
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), ts.toString(), chapterId, sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.RESULT, Constant.SUCCESS_CODE_0);
        CPChapter cpChapter = new CPChapter();
        String content = data.getString("content")
                .replaceAll("<p class=\"tt-novel-speak\">", "")
                .replaceAll("<p class=\"ql-align-justify\">", "")
                .replaceAll("<p class=\"MsoPlainText\">", "")
                .replaceAll("<p class=\"MsoNormal\">", "")
                .replaceAll("<p style=\"text-align: left;\">", "")
                .replaceAll("<p style=\"text-align: left;\" class=\"MsoNormal\">", "")
                .replaceAll("<p style=\"text-align: left;text-indent: 1em;\">", "")
                .replaceAll("<span class=\"ai-hover-green\">", "")
                .replaceAll("</span>", "")
                .replaceAll("<div>", "").replaceAll("</div>", "")
                .replaceAll("<p>", "").replaceAll("</p>", "\n");

        if (StringUtils.contains(content, "<tt_keyword_ad") && StringUtils.contains(content, "</tt_keyword_ad>")) {
            int start = content.indexOf("<tt_keyword_ad");
            int end = content.indexOf("</tt_keyword_ad>");
            String adContent = content.substring(start, end + "</tt_keyword_ad>".length());
            content = content.replaceAll(adContent, "");
        }

        cpChapter.setContent(content);
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JIN_RI_TOU_TIAO};
    }

}
