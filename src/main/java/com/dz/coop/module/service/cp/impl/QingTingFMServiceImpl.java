package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.conf.properties.QingTingFMConf;
import com.dz.coop.module.constant.InterfaceAccessTypeEnum;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.glory.common.tools.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 蜻蜓FM接口对接
 * @author: songwj
 * @date: 2019-11-14 17:09
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class QingTingFMServiceImpl implements ClientService {

    private Logger logger = LoggerFactory.getLogger(QingTingFMServiceImpl.class);

    private static final int SUCCESS_CODE = 0;

    private static final int DEFAULT_PAGE_SIZE = 30;

    private static final int MAX_PAGE_SIZE = 500;

    @Resource
    private QingTingFMConf qingTingFMConf;

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String accessToken = getAccessToken(qingTingFMConf.getUrl());
        String bookListUrl = MessageFormat.format(owchPartner.getBookListUrl(), accessToken);
        JSONObject books = doGet(bookListUrl, InterfaceAccessTypeEnum.BOOK_LIST, JSONObject.class);

        JSONArray bookArr = books.getJSONArray("data");
        Integer total = books.getInteger("total");
        int totalPageSize = total / DEFAULT_PAGE_SIZE + 1;
        List<CPBook> cpBooks = new ArrayList<>();
        addCpBook(bookArr, cpBooks);

        if (totalPageSize >= 2) {
            for (int pageSize = 2; pageSize <= totalPageSize; pageSize++) {
                bookArr = doGet(MessageFormat.format(owchPartner.getBookListUrl() + "&page=" + pageSize, accessToken), InterfaceAccessTypeEnum.BOOK_INFO, JSONArray.class);
                addCpBook(bookArr, cpBooks);
            }
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String accessToken = getAccessToken(qingTingFMConf.getUrl());
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, accessToken), InterfaceAccessTypeEnum.BOOK_INFO, JSONObject.class);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("title"));
        cpBook.setAuthor(data.get("podcasters") != null ? data.getJSONArray("podcasters").getJSONObject(0).getString("nickname") : "佚名");
        cpBook.setBrief(data.getString("description"));
        cpBook.setCover(data.getJSONObject("thumbs").getString("large_thumb"));
        // 点播专辑是否完结(1表示完结，0表示未完结)
        cpBook.setCompleteStatus(data.getString("is_finished"));
        cpBook.setTag(getTags(data.getJSONArray("attributes")));

        if (data.get("purchase_items") != null) {
            JSONObject purchaseItems = data.getJSONArray("purchase_items").getJSONObject(0);
            cpBook.setPrice(purchaseItems.getString("price"));
            // 	商品促销价
            cpBook.setRecommendPrice(purchaseItems.getString("promotional_price"));
            cpBook.setUnit(StringUtils.equals(purchaseItems.getString("item_type"), "channel") ? Book.END_UNIT : Book.SERIAL_UNIT);
        } else {
            cpBook.setPrice("0");
            cpBook.setRecommendPrice("0");
            cpBook.setUnit(Book.SERIAL_UNIT);
        }

        // 点赞数
        cpBook.setPraiseNum(data.getLong("popularity"));
        Map<String, Object> extendMap = new HashMap<>(3);

        if (data.get("purchase_items") != null) {
            // 商品id
            extendMap.put("itemId", data.getJSONArray("purchase_items").getJSONObject(0).getString("item_id"));
        }

        // 专辑评分（范围为0到10）
        extendMap.put("comScore", data.getInteger("star"));
        extendMap.put("showScore", data.getInteger("star"));
        cpBook.setExtend(JSON.toJSONString(extendMap));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String accessToken = getAccessToken(qingTingFMConf.getUrl());
        JSONObject chapters = doGet(MessageFormat.format(owchPartner.getChapterListUrl() + "&pagesize=" + MAX_PAGE_SIZE, bookId, accessToken), InterfaceAccessTypeEnum.CHAPTER_LIST, JSONObject.class);

        JSONArray chapterArr = chapters.getJSONArray("data");
        Integer total = chapters.getInteger("total");
        int totalPageSize = total / MAX_PAGE_SIZE + 1;
        List<CPChapter> cpChapters = new ArrayList<>();
        addCpChapter(chapterArr, cpChapters);

        if (totalPageSize >= 2) {
            for (int pageSize = 2; pageSize <= totalPageSize; pageSize++) {
                chapterArr = doGet(MessageFormat.format(owchPartner.getChapterListUrl() + "&page=" + pageSize + "&pagesize=" + MAX_PAGE_SIZE, bookId, accessToken), InterfaceAccessTypeEnum.CHAPTER_INFO, JSONArray.class);
                addCpChapter(chapterArr, cpChapters);
            }
        }

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String accessToken = getAccessToken(qingTingFMConf.getUrl());
        JSONObject chapterObj = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl() + "&device_id=861322033943628", cpBookId, chapterId, accessToken), InterfaceAccessTypeEnum.CHAPTER_INFO, JSONObject.class);
        CPChapter cpChapter = new CPChapter();

        cpChapter.setContent(chapterObj.getJSONArray("editions").getJSONObject(0).getJSONArray("urls").getString(0));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QING_TING_FM};
    }

    private void addCpBook(JSONArray bookArr, List<CPBook> cpBooks) {
        CPBook cpBook = null;

        for (int i = 0, len = bookArr.size(); i < len; i++) {
            cpBook = new CPBook();
            JSONObject bookObj = bookArr.getJSONObject(i);
            cpBook.setId(bookObj.getString("id"));
            cpBook.setName(bookObj.getString("title"));
            cpBooks.add(cpBook);
        }
    }

    private void addCpChapter(JSONArray chapterArr, List<CPChapter> cpChapters) {
        CPChapter cpChapter = null;

        for (int i = 0, len = chapterArr.size(); i < len; i++) {
            cpChapter = new CPChapter();
            JSONObject chapterObj = chapterArr.getJSONObject(i);
            cpChapter.setId(chapterObj.getString("id"));
            cpChapter.setName(chapterObj.getString("title"));
            cpChapter.setIsFree(chapterObj.getBoolean("is_free") ? 0 : 1);
            cpChapter.setDuration(chapterObj.getInteger("duration"));
            cpChapters.add(cpChapter);
        }
    }

    /**
     * 获取标签，多个以英文逗号分隔
     * @param attributes
     * @return
     */
    private String getTags(JSONArray attributes) {
        StringBuffer tags = new StringBuffer();

        for (int i = 0, len = attributes.size(); i < len; i++) {
            String tagName = attributes.getJSONObject(i).getString("name");

            if (i == len - 1) {
                tags.append(tagName);
            } else {
                tags.append(tagName).append(",");
            }
        }

        return tags.toString();
    }

    private String getAccessToken(String url) throws Exception {
        Map<String, String> requestBody = new HashMap<>(3);
        requestBody.put("grant_type", qingTingFMConf.getGrantType());
        requestBody.put("client_id", qingTingFMConf.getClientId());
        requestBody.put("client_secret", qingTingFMConf.getClientSecret());

        String resp = HttpUtil.doPost(qingTingFMConf.getUrl(), requestBody);

        if (StringUtils.isNotBlank(resp)) {
            JSONObject jsonObject = JSONObject.parseObject(resp);
            int code = jsonObject.getInteger("errcode");

            if (code == SUCCESS_CODE) {
                return jsonObject.getJSONObject("data").getString("access_token");
            } else {
                throw new Exception("获取蜻蜓FM访问令牌失败,errcode=" + code + ",errmsg=" + jsonObject.getString("errmsg"));
            }
        } else {
            throw new Exception("获取蜻蜓FM访问令牌失败，返回的json为空");
        }
    }

    private <T> T doGet(String url, InterfaceAccessTypeEnum interfaceAccessType, Class<T> clazz) throws Exception {
        String resp = HttpUtil.sendGet(url);
        JSONObject json = JSONObject.parseObject(resp);
        Integer code = json.getInteger("errcode");

        if (code != SUCCESS_CODE) {
            throw new Exception("蜻蜓FM接口获取失败，errcode=" + code + ",errmsg=" + json.getString("errmsg"));
        }

        switch (interfaceAccessType) {
            case BOOK_LIST:
                return JsonUtil.readValue(resp, clazz);
            case CHAPTER_LIST:
                return JsonUtil.readValue(resp, clazz);
            default:
                return json.getObject("data", clazz);
        }
    }

}
