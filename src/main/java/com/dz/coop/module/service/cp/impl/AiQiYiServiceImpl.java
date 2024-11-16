package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.template.Template;
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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz 2018-12-07 1:36 PM
 */
@Service
public class AiQiYiServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(AiQiYiServiceImpl.class);

    private static final String SUCCESS_CODE = "A00000";

    private static final String SEPARATOR = "#";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONObject jsonObject = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId()));
        JSONArray books = jsonObject.getJSONArray("books");
        if (books == null || books.isEmpty()) {
            throw new RuntimeException("书籍列表为空");
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId));
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("briefDescription"));
        cpBook.setCover(data.getString("coverImg"));
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("progress"), "2") ? "0" : "1");
        cpBook.setTag(data.getString("label"));
        cpBook.setCategory(data.getString("category"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        JSONObject jsonObject = sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId));
        JSONArray volumes = jsonObject.getJSONArray("volumes");
        if (volumes == null || volumes.isEmpty()) {
            throw new RuntimeException("bookId=" + bookId + "书籍章节列表为空");
        }

        List<CPVolume> cpVolumes = new ArrayList<>(volumes.size());
        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            String volumeId = volume.getString("volumeId");
            String volumeTitle = volume.getString("volumeTitle");
            cpVolume.setId(volumeId);
            cpVolume.setName(volumeTitle);

            JSONArray chapters = volume.getJSONArray("chapters");
            if (chapters == null || chapters.isEmpty()) {
                log.warn(Template.EMPTY_CHAPTER_LOG, owchPartner.getName(), bookId, volumeId, volumeTitle);
                continue;
                //throw new RuntimeException("章节列表为空");
            }

            List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
            for (int j = 0; j < chapters.size(); j++) {
                JSONObject chapter = chapters.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.getString("chapterId"));
                cpChapter.setName(chapter.getString("title"));
                cpChapters.add(cpChapter);
            }

            cpVolume.setChapterList(cpChapters);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String random = System.currentTimeMillis() + "";
        JSONObject jsonObject = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), chapterId, sign(chapterId,
                owchPartner.getAliasId(), owchPartner.getApiKey(), random), random));
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(jsonObject.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.AI_QI_YI};
    }

    private JSONObject sendGet(String url) {
        String s = HttpUtil.sendGet(url);
        JSONObject jsonObject = JSON.parseObject(s);
        if (!StringUtils.equals(jsonObject.getString("code"), SUCCESS_CODE)) {
            throw new RuntimeException("通信异常");
        }
        return jsonObject.getJSONObject("data");
    }

    private String sign(String chapterId, String identity, String key, String verify) {
        String str1 = DigestUtils.md5Hex(chapterId + SEPARATOR + identity + SEPARATOR + key);
        return DigestUtils.md5Hex(verify + SEPARATOR + str1);
    }

}
