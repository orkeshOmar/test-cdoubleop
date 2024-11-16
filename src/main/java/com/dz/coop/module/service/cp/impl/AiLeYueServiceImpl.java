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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2017-10-10 上午9:50
 */
@Service
public class AiLeYueServiceImpl implements ClientService {

    private static final String USER_NAME = "dianzhong";

    private static final String USER_KEY = "e6a5cca45fda86f794b09140b97dd980";

    private static final String sign;

    static {
        sign = DigestUtils.md5Hex(USER_NAME + USER_KEY);
    }

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        JSONArray books = getDataFromRequest(MessageFormat.format(owchPartner.getBookListUrl(), USER_NAME, sign), JSONArray.class);
        if (CollectionUtils.isEmpty(books)) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0, length = books.size(); i < length; i++) {
            JSONObject object = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(object.getString("bookId"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        JSONObject data = getDataFromRequest(MessageFormat.format(owchPartner.getBookInfoUrl(), USER_NAME, sign, bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("introduce"));
        cpBook.setCover(data.getString("coverPath"));
        cpBook.setCompleteStatus("0".equals(data.getString("serialProp")) ? "1" : "0");

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        JSONArray chapters = getDataFromRequest(MessageFormat.format(owchPartner.getChapterListUrl(), USER_NAME, sign, bookId), JSONArray.class);
        if (CollectionUtils.isEmpty(chapters)) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
        for (int i = 0, length = chapters.size(); i < length; i++) {
            JSONObject object = chapters.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(object.getString("sequence"));
            cpChapter.setName(object.getString("name"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        String content = getDataFromRequest(MessageFormat.format(owchPartner.getChapterInfoUrl(), USER_NAME, sign, cpBookId, chapterId), String.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(content.replaceAll("<br/>", "\n"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIA_MEN_AI_YUE};
    }

    private <T> T getDataFromRequest(String url, Class<T> clazz) throws Exception {
        JSONObject resp = JSONObject.parseObject(HttpUtil.sendGet(url));
        String retCode = resp.getString("code");
        if (!"0".equals(retCode)) {
            throw new Exception("爱乐阅返回失败,retCode=" + retCode + ",retMsg=" + resp.getString("desc"));
        }
        return resp.getObject("data", clazz);
    }

}
