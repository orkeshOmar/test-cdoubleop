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
 * @author panqz
 * @date 2017-08-31 上午11:06
 */
@Service
public class ChuYueServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        String response = HttpUtil.sendGet(owchPartner.getBookListUrl());

        JSONArray data = JSONArray.parseArray(response);
        if (null == data || data.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject book = data.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookId"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String response = HttpUtil.sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId));

        JSONObject bookInfo = JSONObject.parseObject(response);

        CPBook cpBook = new CPBook();
        cpBook.setId(bookInfo.getString("bookId"));
        cpBook.setName(bookInfo.getString("bookName"));
        cpBook.setAuthor(bookInfo.getString("authorName"));
        cpBook.setBrief(bookInfo.getString("description"));
        cpBook.setCover(bookInfo.getString("cover"));
        cpBook.setCompleteStatus(bookInfo.getString("finish"));
//
        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String response = HttpUtil.sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), bookId));

        JSONArray data = JSONArray.parseArray(response);
        if (null == data || data.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume previousCpvolume = null;
        for (int i = 0; i < data.size(); i++) {
            JSONObject chapter = data.getJSONObject(i);
            String cpVolumeId = chapter.getString("tomeId");
            String cpVolumeName = chapter.getString("tomeName");
            if (!(previousCpvolume != null && StringUtils.equals(previousCpvolume.getId(), cpVolumeId))) {
                previousCpvolume = new CPVolume(cpVolumeId, cpVolumeName);
                cpVolumes.add(previousCpvolume);
            }
            List<CPChapter> cpChapters = previousCpvolume.getChapterList();
            if (cpChapters == null) {
                cpChapters = new ArrayList<>();
            }
            previousCpvolume.setChapterList(cpChapters);
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterId"));
            cpChapter.setName(chapter.getString("chapterName"));
            cpChapters.add(cpChapter);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        String response = HttpUtil.sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), chapterId));

        JSONObject object = JSONObject.parseObject(response);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(object.getString("chapterContent"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.FANG_XING};
    }
}
