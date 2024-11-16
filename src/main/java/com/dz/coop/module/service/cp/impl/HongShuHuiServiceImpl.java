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
import com.dz.glory.common.utils.DateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author panqz 2018-02-08 下午1:29
 * @date : 2020-07-16 修改为对接我方接口
 */
@Deprecated
//@Service
public class HongShuHuiServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(HongShuHuiServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), getSign(owchPartner)), JSONArray.class);
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject object = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(object.getString("bookId"));
            cpBook.setName(object.getString("book_name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), getSign(owchPartner), bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("book_name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("brief"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(data.getString("status"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), getSign(owchPartner), bookId), JSONArray.class);

        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject volume = data.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId("0");
            cpVolume.setName(volume.getString("volumeName"));

            JSONArray chapters = volume.getJSONArray("chapterlist");
            if (chapters == null || chapters.isEmpty()) {
                logger.error("bookId={},volumeId={}章节列表为空", bookId, cpVolume.getId());
                return Collections.emptyList();
            }

            List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
            for (int j = 0; j < chapters.size(); j++) {
                JSONObject chapter = chapters.getJSONObject(j);
                CPChapter cpChapte = new CPChapter();
                cpChapte.setId(chapter.getString("chapter_id"));
                cpChapte.setName(chapter.getString("chapter_name"));
                cpChapters.add(cpChapte);
            }

            cpVolume.setChapterList(cpChapters);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), getSign(owchPartner), cpBookId, chapterId), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    private <T> T doGet(String url, Class<T> clazz) {
        String resp = HttpUtil.sendGet(url);
        JSONObject respObject = JSONObject.parseObject(resp);
        String code = respObject.getString("code");
        if (!"200".equals(code)) {
            throw new RuntimeException("红书汇通信异常,code=" + code + ",msg=" + respObject.getString("msg"));
        }
        return respObject.getObject("data", clazz);
    }

    private String getSign(Partner owchPartner) {
        String sid = owchPartner.getAliasId();
        String key = owchPartner.getApiKey();
        String yyyyMMdd = DateUtil.format(new Date(), "yyyyMMdd");
        return DigestUtils.md5Hex(yyyyMMdd + sid + key);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HONG_SHU_HUI};
    }
}
