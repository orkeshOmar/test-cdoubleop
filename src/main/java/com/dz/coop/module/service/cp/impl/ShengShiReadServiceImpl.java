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
import java.util.Collections;
import java.util.List;


/**
 * @author panqz
 * @create 2017-08-16 下午3:39
 */
@Service
public class ShengShiReadServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, owchPartner.getApiKey());
        JSONArray data = JSONArray.parseArray(HttpUtil.sendGet(bookListUrl));
        if (data == null || data.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            String bookId = item.getString("bookid");
            String title = item.getString("title");
            CPBook cpBook = new CPBook();
            cpBook.setId(bookId);
            cpBook.setName(title);
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, owchPartner.getApiKey(), bookId);
        JSONObject data = JSONObject.parseObject(HttpUtil.sendGet(bookInfoUrl));

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookid"));
        cpBook.setName(data.getString("title"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getString("cover_url"));
        cpBook.setCategory(data.getString("class"));// 所属分类
        cpBook.setCompleteStatus(data.getString("bookstatus"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, owchPartner.getApiKey(), bookId);
        JSONArray data = JSONArray.parseArray(HttpUtil.sendGet(chapterListUrl));
        if (data == null || data.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> columeList = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject object = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(object.getString("chapterid"));
            cpChapter.setName(object.getString("chaptername"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        columeList.add(cpVolume);

        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        String chapterInfoUrl = owchPartner.getChapterInfoUrl();
        chapterInfoUrl = MessageFormat.format(chapterInfoUrl, owchPartner.getApiKey(), cpBookId, chapterId);
        JSONObject data = JSONObject.parseObject(HttpUtil.sendGet(chapterInfoUrl));

        CPChapter cpChapter = new CPChapter();
        cpChapter.setName(data.getString("chaptername"));
        cpChapter.setContent(data.getString("chaptercontent"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHENG_SHI};
    }
}
