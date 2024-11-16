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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-01-04 下午2:06
 */
@Service
public class YueHaoWanServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray data = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey()), JSONArray.class);
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject book = data.getJSONObject(i);
            CPBook cpBook = new CPBook(book.getString("articleid"), book.getString("articlename"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId), JSONObject.class);


        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(data.getString("fullflag"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId), JSONArray.class);
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> list = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter(chapter.getString("chapterId"), chapter.getString("chaptername"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        list.add(cpVolume);
        return list;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId), JSONObject.class);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);

    }

    private <T> T doGet(String url, Class<T> clazz) {
        JSONObject json = JSONObject.parseObject(HttpUtil.sendGet(url));
        String code = json.getString("code");
        if (!StringUtils.equals(code, "200")) {
            throw new RuntimeException("阅好玩接口异常，code=" + code + ",msg=" + json.getString("error"));
        }
        return json.getObject("data", clazz);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YUE_HAO_WAN};
    }
}
