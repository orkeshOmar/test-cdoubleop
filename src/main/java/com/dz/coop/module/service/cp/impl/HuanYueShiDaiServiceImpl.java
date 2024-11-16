package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.template.Template;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz
 * @create 2017-07-05 上午9:51
 */
@Service
public class HuanYueShiDaiServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(HuanYueShiDaiServiceImpl.class);

    private static final String CLIENT_ID = "16";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, CLIENT_ID, owchPartner.getApiKey());
        String resp = HttpUtil.sendGet(bookListUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"success".equals(json.getString("status"))) {
            throw new Exception(json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");
        JSONArray books = data.getJSONArray("books");

        List<CPBook> cpBooks = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookId"));
            cpBook.setName(book.getString("bookName"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, CLIENT_ID, owchPartner.getApiKey(), bookId);
        String resp = HttpUtil.sendGet(bookInfoUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"success".equals(json.getString("status"))) {
            throw new Exception(json.getString("message"));
        }

        JSONObject data = json.getJSONObject("data");
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("authorName"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("bookPic"));
        cpBook.setCategory(data.getString("category"));// 所属分类
        cpBook.setCompleteStatus(String.valueOf(data.getInteger("progress")));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, CLIENT_ID, owchPartner.getApiKey(), bookId);
        String resp = HttpUtil.sendGet(chapterListUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"success".equals(json.getString("status"))) {
            throw new Exception(json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");
        JSONArray volumes = data.getJSONArray("volumes");

        List<CPVolume> columeList = new ArrayList<>();
        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            String volumeId = volume.getString("volumeId");
            String volumeTitle = volume.getString("volumeName");
            cpVolume.setId(volumeId);
            cpVolume.setName(volumeTitle);
            JSONArray chapterList = volume.getJSONArray("chapters");

            if (chapterList == null || chapterList.size() == 0) {
                log.warn(Template.EMPTY_CHAPTER_LOG, owchPartner.getName(), bookId, volumeId, volumeTitle);
                continue;
            }

            List<CPChapter> cpChapters = new ArrayList<>();
            for (int j = 0; j < chapterList.size(); j++) {
                JSONObject chapter = chapterList.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.getString("chapterId"));
                cpChapter.setName(chapter.getString("title"));
                cpChapters.add(cpChapter);

            }
            cpVolume.setChapterList(cpChapters);
            columeList.add(cpVolume);
        }
        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterinfoUrl = owchPartner.getChapterInfoUrl();
        chapterinfoUrl = MessageFormat.format(chapterinfoUrl, CLIENT_ID, owchPartner.getApiKey(), cpBookId, chapterId);
        String resp = HttpUtil.sendGet(chapterinfoUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"success".equals(json.getString("status"))) {
            throw new Exception(json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");

        CPChapter cpChapter = new CPChapter();
        String content = data.getString("content");
        cpChapter.setContent(content.replaceAll("<p>", "").replaceAll("</p>", "\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HUAN_YUE_SHI_DAI};
    }

}
