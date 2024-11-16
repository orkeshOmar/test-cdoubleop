package com.dz.coop.module.service.cp.impl;

import com.alibaba.druid.util.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-06-26 上午9:43
 */
@Service
public class TianYueServiceImpl implements ClientService {

    private static final String TIME_STAMPT = getTimeStampt();


    private static final String SUCCESS_CODE = "T200";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey(), TIME_STAMPT));
        JSONArray books = data.getJSONArray("bookList");

        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("bookId"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId));
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("authorName"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(StringUtils.equals("1", data.getString("bookStatus")) ? "1" : "0");
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId));
        JSONArray volumeList = data.getJSONArray("volumeList");
        if (volumeList == null || volumeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>(volumeList.size());
        for (int i = 0; i < volumeList.size(); i++) {
            JSONObject volume = volumeList.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(volume.getString("volumeOrd"));
            cpVolume.setName(volume.getString("volumeName"));
            JSONArray chapters = volume.getJSONArray("chapterList");
            if (chapters != null && chapters.size() > 0) {
                List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
                for (int j = 0; j < chapters.size(); j++) {
                    JSONObject jsonObject = chapters.getJSONObject(j);
                    CPChapter cpChapter = new CPChapter();
                    cpChapter.setId(jsonObject.getString("chapterId"));
                    cpChapters.add(cpChapter);
                }
                cpVolume.setChapterList(cpChapters);

            } else {
                cpVolume.setChapterList(Collections.<CPChapter>emptyList());
            }
            cpVolumes.add(cpVolume);
        }
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        JSONObject chapter = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId));

        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(chapterId);
        cpChapter.setName(chapter.getString("chapterName"));
        cpChapter.setContent(chapter.getString("content"));

        return cpChapter;


    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.TIAN_YUE};
    }

    private JSONObject sendGet(String url) throws Exception {
        JSONObject object = JSONObject.parseObject(HttpUtil.sendGet(url));
        String status = object.getString("code");
        if (!StringUtils.equals(status, SUCCESS_CODE)) {
            throw new Exception("天路接口异常,status=" + status + ",message=" + object.getString("message"));
        }
        return object.getJSONObject("data");
    }


    private static String getTimeStampt() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hH:mm:ss");
        try {
            return simpleDateFormat.parse("2010-01-01 01:00:00").getTime() + "";
        } catch (Exception e) {
            return "";
        }
    }
}
