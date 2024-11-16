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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * @author panqz 2017-10-26 下午1:52
 */
@Service
public class ShuChuServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        JSONArray books = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey()), "booklist", JSONArray.class);
        if (books == null || books.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpbook = new CPBook();
            cpbook.setId(book.getString("bookid"));
            cpBooks.add(cpbook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        JSONObject book = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId), null, JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(book.getString("id"));
        cpBook.setName(book.getString("name"));
        cpBook.setAuthor(book.getString("author"));
        cpBook.setBrief(book.getString("bookintr"));
        cpBook.setCover(book.getString("bigimg"));
        cpBook.setCompleteStatus(book.getString("bookstatus"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String[] chapterUrl = owchPartner.getChapterListUrl().split(";");
        JSONArray volumes = sendGet(MessageFormat.format(chapterUrl[0], owchPartner.getApiKey(), bookId), "volumelist", JSONArray.class);

        TreeMap<Integer, CPVolume> volumeMap = new TreeMap<>();

        if (volumes != null && volumes.size() > 0) {
            for (int i = 0; i < volumes.size(); i++) {
                JSONObject volume = volumes.getJSONObject(i);
                CPVolume cpVolume = new CPVolume();
                cpVolume.setId(volume.getString("volumeid"));
                cpVolume.setName(volume.getString("volumename"));
                volumeMap.put(volume.getInteger("volumeid"), cpVolume);
            }
        }

        JSONArray chapters = sendGet(MessageFormat.format(chapterUrl[1], owchPartner.getApiKey(), bookId), "chapterlist", JSONArray.class);

        if (chapters == null || chapters.size() == 0) {
            return Collections.emptyList();
        }

        for (int i = 0; i < chapters.size(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterid"));
            cpChapter.setName(chapter.getString("chaptername"));

            String volumeId = chapter.getString("volumeid");

            CPVolume cpVolume;
            if (StringUtils.isNotBlank(volumeId)) {
                cpVolume = volumeMap.get(Integer.parseInt(volumeId));
                if (cpVolume == null) {
                    throw new Exception("卷id为" + volumeId + "未找到卷信息");
                }
            } else {
                cpVolume = volumeMap.get(-1);
                if (cpVolume == null) {
                    CPVolume cpVolume1 = new CPVolume();
                    cpVolume1.setId("1");
                    cpVolume1.setName("正文");
                    volumeMap.put(-1, cpVolume1);
                }
            }
            List<CPChapter> cpChapters = cpVolume.getChapterList();
            if (cpChapters == null) {
                cpChapters = new ArrayList<>();
                cpVolume.setChapterList(cpChapters);
            }
            cpChapters.add(cpChapter);
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        cpVolumes.addAll(volumeMap.values());

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        JSONObject chapter = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), chapterId, cpBookId), null, JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(chapter.getString("chapterid"));
        cpChapter.setContent(chapter.getString("content"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHU_CHU};
    }

    private <T> T sendGet(String url, String key, Class<T> clazz) {
        if (StringUtils.isNotBlank(key)) {
            JSONObject json = JSON.parseObject(HttpUtil.sendGet(url));
            return json.getObject(key, clazz);
        }

        return JSON.parseObject(HttpUtil.sendGet(url), clazz);
    }
}
