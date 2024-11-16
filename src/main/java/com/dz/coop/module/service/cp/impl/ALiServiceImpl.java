package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.MD5Util;
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
import java.util.*;

/**
 * @author panqz
 * @create 2017-03-30 上午9:57
 */
@Service
public class ALiServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ALiServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String apiKey = owchPartner.getApiKey();
        String cpId = String.valueOf(owchPartner.getAliasId());
        String timeStamp = getTimeStamp();
        Map<String, String> paras = new HashMap<>();
        paras.put("cpId", cpId);
        paras.put("timestamp", timeStamp);
        String sign = getSignString(paras, apiKey);
        String patternUrl = owchPartner.getBookListUrl() + "/?cpId={0}&sign={1}&timestamp={2}";
        String url = MessageFormat.format(patternUrl, cpId, sign, timeStamp);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String status = json.getString("status");
        if (!"200".equals(status)) {
            throw new Exception("error message from ali : " + json.getString("message"));
        }
        JSONArray array = json.getJSONArray("data");
        if (array == null || array.size() == 0) {
            throw new Exception("ali books size is 0");
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            String bookId = item.getString("bookId");
            String bookName = item.getString("bookName");
            CPBook cpBook = new CPBook();
            cpBook.setId(bookId);
            cpBook.setName(bookName);
            cpBooks.add(cpBook);
        }
        return cpBooks;


    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String apiKey = owchPartner.getApiKey();
        String cpId = String.valueOf(owchPartner.getAliasId());
        String timeStamp = getTimeStamp();
        Map<String, String> paras = new HashMap<>();
        paras.put("cpId", cpId);
        paras.put("bookId", bookId);
        paras.put("timestamp", timeStamp);
        String sign = getSignString(paras, apiKey);
        String patternUrl = owchPartner.getBookInfoUrl() + "/?bookId={0}&cpId={1}&sign={2}&timestamp={3}";
        String url = MessageFormat.format(patternUrl, bookId, cpId, sign, timeStamp);
        logger.info("ali url : {}", url);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String status = json.getString("status");
        if (!"200".equals(status)) {
            throw new Exception("error message from ali : " + json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new Exception("ali bookinfo is null");
        }
        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("bookName"));
        cpBook.setAuthor(data.getString("authorName"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));
        // 所属二级分类
        cpBook.setCategory(data.getString("secondCategory"));
        String bookStatus = data.getString("bookStatus");
        if ("1".equals(bookStatus)) {
            cpBook.setCompleteStatus("0");
        } else {
            cpBook.setCompleteStatus("1");
        }
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String apiKey = owchPartner.getApiKey();
        String cpId = String.valueOf(owchPartner.getAliasId());
        String timeStamp = getTimeStamp();
        Map<String, String> paras = new HashMap<>();
        paras.put("cpId", cpId);
        paras.put("bookId", bookId);
        paras.put("timestamp", timeStamp);
        String sign = getSignString(paras, apiKey);
        String patternUrl = owchPartner.getChapterListUrl() + "/?bookId={0}&cpId={1}&sign={2}&timestamp={3}&volumeInfo={4}";
        String url = MessageFormat.format(patternUrl, bookId, cpId, sign, timeStamp, "Y");
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String status = json.getString("status");
        if (!"200".equals(status)) {
            throw new Exception("error message from ali : " + json.getString("message"));
        }
        JSONArray data = json.getJSONArray("data");
        if (data == null || data.size() == 0) {
            throw new Exception("chapters is null");
        }
        List<CPVolume> columeList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject volume = data.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(volume.getString("volumeId"));
            cpVolume.setName(volume.getString("volumeName"));

            JSONArray chapterList = volume.getJSONArray("chapterList");
            if (chapterList == null || chapterList.size() == 0) {
                continue;
            }
            List<CPChapter> cpChapters = new ArrayList<>();
            for (int j = 0; j < chapterList.size(); j++) {
                JSONObject chapter = chapterList.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.getString("chapterId"));
                cpChapter.setName(chapter.getString("chapterName"));
                cpChapters.add(cpChapter);

            }
            cpVolume.setChapterList(cpChapters);
            columeList.add(cpVolume);
        }
        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String apiKey = owchPartner.getApiKey();
        String cpId = String.valueOf(owchPartner.getAliasId());
        String timeStamp = getTimeStamp();
        Map<String, String> paras = new HashMap<>();
        paras.put("cpId", cpId);
        paras.put("bookId", cpBookId);
        paras.put("timestamp", timeStamp);
        paras.put("chapterId", chapterId);
        String sign = getSignString(paras, apiKey);
        String patternUrl = owchPartner.getChapterInfoUrl() + "/?bookId={0}&chapterId={1}&cpId={2}&sign={3}&timestamp={4}";
        String url = MessageFormat.format(patternUrl, cpBookId, chapterId, cpId, sign, timeStamp);
        String response = HttpUtil.sendGet(url);
        JSONObject json = JSON.parseObject(response);
        String status = json.getString("status");
        if (!"200".equals(status)) {
            throw new Exception("error message from ali : " + json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new Exception("ali chapterinfo is null");
        }
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ALI_WEN_XUE, ThirdPart.SHU_QI_XIAO_SHUO};
    }

    private String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis()).substring(0, 10);
    }

    private String getSignString(Map<String, String> paras, String apiKey) {
        if (paras == null || StringUtils.isBlank(apiKey))
            return null;
        Set<String> keys = paras.keySet();
        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (String key : list) {
            String value = paras.get(key);
            sb.append(key).append("=").append(value);
        }
        sb.append(apiKey);
        return MD5Util.MD5(sb.toString()).toLowerCase();
    }
}
