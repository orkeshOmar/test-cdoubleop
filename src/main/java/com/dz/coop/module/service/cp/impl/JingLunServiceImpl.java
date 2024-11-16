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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-03-30 下午5:47
 */
//@Service
public class JingLunServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(JingLunServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getBookListUrl(), appId, sign(owchPartner.getApiKey(), appId)), JSONArray.class);
        if (data == null || data.size() < 0) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject book = data.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), appId, bookId, sign(owchPartner.getApiKey(), appId, bookId)), JSONObject.class);
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("introduce"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("state"), "1") ? "0" : "1");
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), appId, bookId, sign(owchPartner.getApiKey(), appId, bookId)), JSONArray.class);
        if (data == null || data.size() < 0) {
            return Collections.emptyList();
        }

        List<CPChapter> cpChapters = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("id"));
            cpChapter.setName(chapter.getString("name"));
            cpChapters.add(cpChapter);
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String appId = owchPartner.getAliasId();
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), appId, cpBookId, chapterId, sign(owchPartner.getApiKey(), appId, cpBookId, chapterId)), JSONObject.class);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content").replaceAll("<br />", "\r\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JING_LUN};
    }

    private <T> T doGet(String url, Class<T> clazz) throws Exception {
        JSONObject json = JSONObject.parseObject(HttpUtil.sendGet(url));
        Integer status = json.getInteger("errorCode");
        if (0 != status) {
            throw new Exception("接口获取失败，status=" + status + ",msg=" + json.getString("msg"));
        }
        return json.getObject("data", clazz);
    }

    private String sign(String appkey, String... paras) {
        StringBuilder sb = new StringBuilder();
        for (String para : paras) {
            sb.append(para).append("&");
        }
        sb.append(appkey);
        return DigestUtils.md5Hex(sb.toString());
    }
}
