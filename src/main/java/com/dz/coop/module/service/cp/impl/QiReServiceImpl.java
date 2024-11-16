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
 * @author panqz 2017-11-22 下午2:00
 */
@Service
public class QiReServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        JSONArray books = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey()), JSONArray.class);
        if (books == null || books.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject object = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(object.getString("id"));
            cpBook.setName(object.getString("name"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("title"));
        cpBook.setCover(data.getString("thumb"));
        cpBook.setCompleteStatus(data.getString("end"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {


        JSONArray volumes = sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId), JSONArray.class);
        if (volumes == null || volumes.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(volumes.size());

        for (int i = 0; i < volumes.size(); i++) {
            JSONObject object = volumes.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(object.getString("id"));
            cpChapter.setName(object.getString("charpterName"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        JSONObject data = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n\r"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QI_RE};
    }

    private <T> T sendGet(String url, Class<T> clazz) {

        JSONObject object = JSON.parseObject(HttpUtil.sendGet(url));
        if (!"1".equals(object.getString("status"))) {
            throw new RuntimeException("接口通讯失败，status = " + object.getString("status") + ",msg = " + object.getString("msg"));
        }
        return object.getObject("data", clazz);
    }
}
