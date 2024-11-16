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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz
 * @create 2017-05-08 下午1:37
 */
@Service
public class MiYueServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(MiYueServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String bookListUrl = owchPartner.getBookListUrl();
        String response = HttpUtil.sendGet(bookListUrl);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("返回异常，错误码=" + code);
        }
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject book = data.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("articleid"));
            cpBook.setName(book.getString("articlename"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        CPBook cpBook = new CPBook();
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("返回异常，错误码=" + code);
        }
        JSONObject book = json.getJSONObject("data");
        cpBook.setId(book.getString("articleid"));
        cpBook.setName(book.getString("articlename"));
        cpBook.setAuthor(book.getString("author"));
        cpBook.setBrief(book.getString("intro"));
        cpBook.setCover(book.getString("cover"));
        cpBook.setCompleteStatus(StringUtils.equals("2", book.getString("fullflag")) ? "1" : "0");
        cpBook.setTag(book.getString("tags"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> cpVolumes = new ArrayList<>();
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, bookId);
        String response = HttpUtil.sendGet(chapterListUrl);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("返回异常，错误码=" + code);
        }
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("");
        List<CPChapter> chapterlist = new ArrayList<>();
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterid"));
            cpChapter.setName(chapter.getString("chaptername"));
            chapterlist.add(cpChapter);
        }
        cpVolume.setChapterList(chapterlist);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        CPChapter cpChapter = new CPChapter();
        String chapterInfofUrl = owchPartner.getChapterInfoUrl();
        chapterInfofUrl = MessageFormat.format(chapterInfofUrl, cpBookId, chapterId);
        String response = HttpUtil.sendGet(chapterInfofUrl);
        JSONObject json = JSON.parseObject(response);
        String code = json.getString("code");
        if (!"200".equals(code)) {
            throw new Exception("返回异常，错误码=" + code);
        }
        JSONObject data = json.getJSONObject("data");
        cpChapter.setName(data.getString("chaptername"));
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.MI_YUE};
    }
}
