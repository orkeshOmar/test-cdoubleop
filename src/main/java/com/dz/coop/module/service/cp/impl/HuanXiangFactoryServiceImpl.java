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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz
 * @create 2017-07-12 下午5:06
 */
@Service
public class HuanXiangFactoryServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String bookListUrl = owchPartner.getBookListUrl();
        String resp = HttpUtil.sendGet(bookListUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"A00000".equals(json.getString("code"))) {
            throw new Exception(json.getString("msg"));
        }
        JSONObject data = json.getJSONObject("data");
        JSONArray books = data.getJSONArray("books");

        List<CPBook> cpBooks = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(String.valueOf(book.getInteger("id")));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, bookId);
        String resp = HttpUtil.sendGet(bookInfoUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"A00000".equals(json.getString("code"))) {
            throw new Exception(json.getString("msg"));
        }

        JSONObject data = json.getJSONObject("data");

        CPBook cpBook = new CPBook();
        cpBook.setId(String.valueOf(data.getInteger("id")));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("briefDescription"));
        cpBook.setCover(data.getString("coverImg"));
        cpBook.setCategory(data.getString("category"));// 所属分类
        cpBook.setCompleteStatus(data.getInteger("progress") == 1 ? "1" : "0");

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, bookId);
        String resp = HttpUtil.sendGet(chapterListUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"A00000".equals(json.getString("code"))) {
            throw new Exception(json.getString("msg"));
        }

        List<CPVolume> cpVolumes = new ArrayList<>();

        JSONObject data = json.getJSONObject("data");
        JSONArray volumes = data.getJSONArray("volumes");
        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(String.valueOf(volume.getInteger("volumeId")));
            cpVolume.setName(volume.getString("volumeTitle"));
            List<CPChapter> chapterlist = new ArrayList<>();
            JSONArray chapters = volume.getJSONArray("chapters");
            for (int j = 0; j < chapters.size(); j++) {
                JSONObject chapter = chapters.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(String.valueOf(chapter.getInteger("chapterId")));
                chapterlist.add(cpChapter);
            }
            cpVolume.setChapterList(chapterlist);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterContentUrl = owchPartner.getChapterInfoUrl();
        chapterContentUrl = MessageFormat.format(chapterContentUrl, cpBookId, 1, chapterId);
        String resp = HttpUtil.sendGet(chapterContentUrl);
        JSONObject json = JSONObject.parseObject(resp);
        if (!"A00000".equals(json.getString("code"))) {
            throw new Exception(json.getString("msg"));
        }
        JSONObject data = json.getJSONObject("data");
        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(String.valueOf(data.getInteger("id")));
        cpChapter.setName(data.getString("title"));
        String content = data.getString("content");
        content = content.replaceAll("<p>", "").replaceAll("</p>", "\n\r");
        cpChapter.setContent(content);

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JIU_YU};
    }
}
