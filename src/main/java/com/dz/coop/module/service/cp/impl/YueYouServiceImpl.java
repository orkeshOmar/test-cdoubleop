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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2017-12-29 上午10:18
 */
@Service
public class YueYouServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(owchPartner.getBookListUrl(), JSONArray.class);
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookid"));
            cpBook.setName(book.getString("bookname"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject book = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(book.getString("bookid"));
        cpBook.setName(book.getString("bookname"));
        cpBook.setAuthor(book.getString("authorname"));
        cpBook.setBrief(book.getString("intro"));
        cpBook.setCover(book.getString("bookpic"));
        cpBook.setCompleteStatus(book.getString("fullflag"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray chapters = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), bookId), JSONArray.class);
        if (chapters == null || chapters.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
        for (int i = 0; i < chapters.size(); i++) {
            JSONObject cpChapter = chapters.getJSONObject(i);
            CPChapter chapter = new CPChapter();
            chapter.setId(cpChapter.getString("chapterid"));
            chapter.setName(cpChapter.getString("chaptername"));
            cpChapters.add(chapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        JSONObject chapter = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(chapter.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    private <T> T doGet(String url, Class<T> clazz) {

        JSONObject object = JSON.parseObject(HttpUtil.sendGet(url));
        if (!"8200".equals(object.getString("code"))) {
            throw new RuntimeException("接口通讯失败，status = " + object.getString("code") + ",msg = " + object.getString("message"));
        }
        return object.getObject("result", clazz);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YUE_YOU};
    }

}
