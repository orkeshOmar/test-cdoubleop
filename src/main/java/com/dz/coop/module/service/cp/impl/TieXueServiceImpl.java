package com.dz.coop.module.service.cp.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-01-03 下午4:21
 */
@Service
public class TieXueServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(TieXueServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey(), "booklist", "631126800000"));
        JSONArray books = data.getJSONArray("books");
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
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
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), "bookinfo", bookId));
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("briefDescription"));
        cpBook.setCover(data.getString("coverImg"));
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("progress"), "2") ? "0" : "1");
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), "chapterlist", bookId));
        JSONArray volumes = data.getJSONArray("volumes");
        if (volumes == null || volumes.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>(volumes.size());

        for (int i = 0; i < volumes.size(); i++) {
            CPVolume cpVolume = new CPVolume();
            JSONObject volume = volumes.getJSONObject(i);

            String volumeId = volume.getString("volumeId");
            String volumeTitle = volume.getString("volumeTitle");
            JSONArray chapters = volume.getJSONArray("chapters");

            if (chapters == null || chapters.isEmpty()) {
                log.warn(Template.EMPTY_CHAPTER_LOG, owchPartner.getName(), bookId, volumeId, volumeTitle);
                continue;
            }

            cpVolume.setId(volumeId);
            cpVolume.setName(volumeTitle);
            cpVolumes.add(cpVolume);

            List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
            for (int j = 0; j < chapters.size(); j++) {
                JSONObject chapter = chapters.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.getString("chapterId"));
                cpChapter.setName(chapter.getString("title"));
                cpChapters.add(cpChapter);
            }
            cpVolume.setChapterList(cpChapters);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), "chapter", cpBookId, volumeId, chapterId));
        CPChapter cpChapter = new CPChapter();
        cpChapter.setName(data.getString("title"));
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        return null;
    }

    private JSONObject doGet(String url) {
        JSONObject object = JSONObject.parseObject(HttpUtil.sendGet(url));
        String code = object.getString("code");
        if (!StringUtils.equals(code, "A00000")) {
            throw new RuntimeException("铁血返回状态吗异常,code=" + code + ",msg=" + object.getString("msg"));
        }
        return object.getJSONObject("data");
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.TIE_XUE};
    }

}
