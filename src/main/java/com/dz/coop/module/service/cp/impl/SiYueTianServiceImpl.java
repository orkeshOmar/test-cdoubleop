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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author panqz 2017-11-07 上午11:12
 */
@Service
public class SiYueTianServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(SiYueTianServiceImpl.class);

    private static final String PAGE_NO = "1";

    private static final String PAGE_SIZE = "1000000";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Map<String, String> map = new HashMap<>();
        String timeStamp = System.currentTimeMillis() + "";
        map.put("timestamp", timeStamp);
        map.put("pageNo", PAGE_NO);
        map.put("pageSize", PAGE_SIZE);
        JSONObject object = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), sign(map, owchPartner.getApiKey()), timeStamp, PAGE_NO, PAGE_SIZE), JSONObject.class);
        JSONArray array = object.getJSONArray("list");
        if (array == null || array.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(array.size());

        for (int i = 0; i < array.size(); i++) {
            JSONObject book = array.getJSONObject(i);
            cpBooks.add(new CPBook(book.getString("bookId"), book.getString("bookName")));
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Map<String, String> map = new HashMap<>();
        String timeStamp = System.currentTimeMillis() + "";
        map.put("timestamp", timeStamp);
        map.put("bookId", bookId);
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), sign(map, owchPartner.getApiKey()), timeStamp, bookId), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("authName"));
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getString("coverImg"));
        cpBook.setCategory(data.getString("category"));// 所属分类
        cpBook.setCompleteStatus("1".equals(data.getString("bookStatus")) ? "0" : "1");

        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String[] urls = owchPartner.getChapterListUrl().split(";");

        Map<String, String> map = new HashMap<>();
        String timeStamp = System.currentTimeMillis() + "";
        map.put("timestamp", timeStamp);
        map.put("bookId", bookId);
        JSONArray data = sendGet(MessageFormat.format(urls[0], sign(map, owchPartner.getApiKey()), timeStamp, bookId), JSONArray.class);
        if (data == null || data.size() <= 0) {
            logger.error("卷列表为空");
            return Collections.emptyList();
        }

        Map<String, String> volumeMap = new TreeMap<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject volume = data.getJSONObject(i);
            volumeMap.put(volume.getString("volumeId"), volume.getString("volumeName"));
        }


        Map<String, String> map1 = new HashMap<>();
        map1.put("timestamp", timeStamp);
        map1.put("pageNo", PAGE_NO);
        map1.put("pageSize", PAGE_SIZE);
        map1.put("bookId", bookId);
        JSONObject object = sendGet(MessageFormat.format(urls[1], sign(map1, owchPartner.getApiKey()), timeStamp, PAGE_NO, PAGE_SIZE, bookId), JSONObject.class);
        JSONArray chapters = object.getJSONArray("list");
        if (chapters == null || chapters.size() == 0) {
            logger.error("章节列表为空");
            return Collections.emptyList();
        }


        Map<String, CPVolume> cpVolumeMap = new TreeMap<>();
        for (int i = 0; i < chapters.size(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            String volumeId = chapter.getString("volumeId");
            CPVolume cpVolume = cpVolumeMap.get(volumeId);
            if (cpVolume == null) {
                cpVolume = new CPVolume();
                cpVolume.setId(volumeId);
                String volumeName = volumeMap.get(volumeId);
                if (StringUtils.isBlank(volumeName)) {
                    logger.error("未获取到volumeId=" + chapter.getString("volumeId") + "的卷名称");
                    return Collections.emptyList();
                }
                cpVolume.setName(volumeName);
                cpVolumeMap.put(volumeId, cpVolume);
            }
            List<CPChapter> cpChapters = cpVolume.getChapterList();
            if (cpChapters == null) {
                cpChapters = new ArrayList<>();
                cpVolume.setChapterList(cpChapters);
            }
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("chapterId"));
            cpChapter.setName(chapter.getString("chapterName"));
            cpChapters.add(cpChapter);
        }

        List<CPVolume> volumes = new ArrayList<>(cpVolumeMap.size());
        volumes.addAll(cpVolumeMap.values());

        return volumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        Map<String, String> map = new HashMap<>();
        String timeStamp = System.currentTimeMillis() + "";
        map.put("timestamp", timeStamp);
        map.put("bookId", cpBookId);
        map.put("chapterId", chapterId);
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), sign(map, owchPartner.getApiKey()), timeStamp, cpBookId, chapterId), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content").replaceAll("<p>","\n\r").replaceAll("</p>","\n\r"));
        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }



    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SI_YUE_TIAN};
    }

    private <T> T sendGet(String url, Class<T> clazz) {
        JSONObject json = JSON.parseObject(HttpUtil.sendGet(url));
        String code = json.getString("code");
        if (!"0".equals(code)) {
            throw new RuntimeException("四月天追更失败，code = " + code + ", msg = " + json.getString("msg"));
        }

        return json.getObject("data", clazz);
    }


    private String sign(Map<String, String> paras, String key) {
        if (MapUtils.isEmpty(paras)) {
            return null;
        }

        TreeMap<String, String> keys = new TreeMap<>(paras);
        Set<Map.Entry<String, String>> entries = keys.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();

        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        sb.append(key);

        return DigestUtils.md5Hex(sb.toString());
    }
}
