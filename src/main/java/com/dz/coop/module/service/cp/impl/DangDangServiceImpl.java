package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.InterfaceAccessTypeEnum;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author panqz 2018-08-02 下午5:24
 */
@Service
public class DangDangServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(DangDangServiceImpl.class);

    private static final String SUCCESS_CODE = "0";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> books = recursionGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey()), "bookUpdateTimestamp", "bookList", null, new CallBack<CPBook, JSONObject>() {
            @Override
            public CPBook doWith(JSONObject data) {
                CPBook cpBook = new CPBook();
                cpBook.setId(data.getString("bookId"));
                cpBook.setName(data.getString("title"));
                cpBook.setAuthor(data.getString("authorName"));
                cpBook.setBrief(data.getString("description"));
                cpBook.setCover(data.getString("coverPic"));
                cpBook.setCompleteStatus(data.getString("isFull"));
                return cpBook;
            }
        });

        return books;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        JSONObject book = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), "book", JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(book.getString("bookId"));
        cpBook.setName(book.getString("title"));
        cpBook.setAuthor(book.getString("authorName"));
        cpBook.setBrief(book.getString("description"));
        cpBook.setCover(book.getString("coverPic"));
        cpBook.setCompleteStatus(book.getString("isFull"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPChapter> cpChapters = recursionGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), bookId), "timestamp", "chapterList", null, new CallBack<CPChapter, JSONObject>() {
            @Override
            public CPChapter doWith(JSONObject data) {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(data.getString("providerChapterId"));
                cpChapter.setName(data.getString("title"));
                cpChapter.setContent(data.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n").replaceAll("\\t", ""));
                return cpChapter;
            }
        });
        if (CollectionUtils.isEmpty(cpChapters)) {
            return Collections.emptyList();
        }
        Collections.sort(cpChapters, new Comparator<CPChapter>() {
            @Override
            public int compare(CPChapter o1, CPChapter o2) {
                Long id1 = Long.parseLong(o1.getId());
                Long id2 = Long.parseLong(o2.getId());
                return id1.compareTo(id2);
            }
        });

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
        JSONObject chapter = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), cpBookId, chapterId), "chapter", JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(chapter.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n").replaceAll("\\t", ""));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.DANG_DANG};
    }

    private <T> List<T> recursionGet(String base, String field, String arrayField, List<T> contents, CallBack<T, JSONObject> callBack) {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        try {
            String json = HttpUtil.sendGet(base);
            JSONObject object = JSON.parseObject(json);
            if (StringUtils.equals(SUCCESS_CODE, object.getJSONObject("status").getString("code"))) {
                JSONObject data = object.getJSONObject("data");
                JSONArray list = data.getJSONArray(arrayField);
                Long time = null;
                if (list == null || list.isEmpty()) {
                    return contents;
                }
                for (int i = 0; i < list.size(); i++) {
                    JSONObject item = list.getJSONObject(i);

                    boolean isInvalidChapter = StringUtils.equals(arrayField, InterfaceAccessTypeEnum.CHAPTER_LIST.getType()) && (item.getIntValue("delFlag") == 1 || item.getString("content").length() < 20);

                    if (!isInvalidChapter) {
                        contents.add(callBack.doWith(item));
                    }

                    if (i == list.size() - 1) {
                        time = item.getLong(field);
                    }
                }
                if (data.getInteger("hasMore") == 1) {
                    if (!base.contains("lastTimestamp")) {
                        base += "&lastTimestamp=" + String.valueOf(time);
                    } else {
                        base = base.replaceAll("(.*&lastTimestamp=).*$", "$1" + String.valueOf(time));
                    }
                    recursionGet(base, field, arrayField, contents, callBack);
                } else {
                    return contents;
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return contents;

    }

    interface CallBack<T, S> {
        T doWith(S s);
    }

    private <T> T doGet(String url, String field, Class<T> clazz) {
        String resp = HttpUtil.sendGet(url);

        if (StringUtils.isBlank(resp)) {
            throw new RuntimeException("[当当]接口返回数据为空");
        }

        JSONObject respObject = JSONObject.parseObject(resp);
        String code = respObject.getJSONObject("status").getString("code");

        if (!StringUtils.equals(SUCCESS_CODE, code)) {
            throw new RuntimeException("[当当]接口通信异常,code=" + code + ",message=" + respObject.getJSONObject("status").getString("message"));
        }

        return respObject.getJSONObject("data").getObject(field, clazz);
    }

}
