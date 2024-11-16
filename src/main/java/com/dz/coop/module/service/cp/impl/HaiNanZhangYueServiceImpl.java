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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 海南掌阅
 * @author: songwj
 * @date: 2023-08-01 2:10 下午
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2023 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class HaiNanZhangYueServiceImpl implements ClientService {

    private static final int PAGE_SIZE = 500;

    /**
     * 书籍列表参数拼接，因&curren在前端页面会被转义为特殊字符，故不在前端作为配置参数
     */
    private static final String BOOK_LIST_URL_PATTERN = "&currentPage={2}&pageSize={3}";

    private static final String CHAPTER_LIST_URL_PATTERN = "&currentPage={3}&pageSize={4}";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String aliasId = owchPartner.getAliasId();
        String sign = getSign(aliasId, owchPartner.getApiKey());
        String urlPattern = owchPartner.getBookListUrl() + BOOK_LIST_URL_PATTERN;
        String url = MessageFormat.format(urlPattern, aliasId, sign, 1, PAGE_SIZE);
        JSONObject data = JSON.parseObject(HttpUtil.sendGet(url));
        JSONArray bookArr = data.getJSONArray("data");
        List<CPBook> bookList = new ArrayList<>();

        for (int n = 0; n < bookArr.size(); n++) {
            JSONObject book = bookArr.getJSONObject(n);
            bookList.add(new CPBook(book.getString("bookId"), book.getString("name")));
        }

        JSONObject page = data.getJSONObject("page");
        int count = page.getIntValue("count");
        int totalPage = count % PAGE_SIZE == 0 ? count / PAGE_SIZE : count / PAGE_SIZE + 1;
        if (totalPage > 1) {
            for (int p = 2; p <= totalPage; p++) {
                url = MessageFormat.format(urlPattern, aliasId, sign, p, PAGE_SIZE);
                data = JSON.parseObject(HttpUtil.sendGet(url));
                bookArr = data.getJSONArray("data");

                for (int n = 0; n < bookArr.size(); n++) {
                    JSONObject book = bookArr.getJSONObject(n);
                    bookList.add(new CPBook(book.getString("bookId"), book.getString("name")));
                }
            }
        }

        return bookList;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String cpBookId) throws Exception {
        String aliasId = owchPartner.getAliasId();
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), aliasId, getSign(aliasId, owchPartner.getApiKey(), cpBookId), cpBookId);
        JSONObject data = JSON.parseObject(HttpUtil.sendGet(url));

        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("bookId"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("brief"));
        cpBook.setCover(data.getString("cover"));
        String bookStatus = data.getString("completeStatus");
        cpBook.setCompleteStatus(StringUtils.equals("Y", bookStatus) ? "1" : "0");
        String categoryId = data.getString("categoryId");
        cpBook.setCategory(StringUtils.isNotBlank(categoryId) ? categoryId.split(",")[0] : "");
        cpBook.setTag(data.getString("keywords"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String cpBookId) throws Exception {
        String aliasId = owchPartner.getAliasId();
        String sign = getSign(aliasId, owchPartner.getApiKey(), cpBookId);
        String urlPattern = owchPartner.getChapterListUrl() + CHAPTER_LIST_URL_PATTERN;
        String url = MessageFormat.format(urlPattern, aliasId, sign, cpBookId, 1, PAGE_SIZE);
        JSONObject data = JSON.parseObject(HttpUtil.sendGet(url));
        JSONArray chapterArr = data.getJSONArray("data");
        List<CPChapter> chapterList = new ArrayList<>();

        for (int n = 0; n < chapterArr.size(); n++) {
            JSONObject book = chapterArr.getJSONObject(n);
            chapterList.add(new CPChapter(book.getString("chapterId"), book.getString("title")));
        }

        JSONObject page = data.getJSONObject("page");
        int count = page.getIntValue("count");
        int totalPage = count % PAGE_SIZE == 0 ? count / PAGE_SIZE : count / PAGE_SIZE + 1;
        if (totalPage > 1) {
            for (int p = 2; p <= totalPage; p++) {
                url = MessageFormat.format(urlPattern, aliasId, sign, cpBookId, p, PAGE_SIZE);
                data = JSON.parseObject(HttpUtil.sendGet(url));
                chapterArr = data.getJSONArray("data");

                for (int n = 0; n < chapterArr.size(); n++) {
                    JSONObject book = chapterArr.getJSONObject(n);
                    chapterList.add(new CPChapter(book.getString("chapterId"), book.getString("title")));
                }
            }
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(chapterList);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpChapterId) throws Exception {
        String aliasId = owchPartner.getAliasId();
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), aliasId, getSign(aliasId, owchPartner.getApiKey(), cpBookId, cpChapterId), cpBookId, cpChapterId);
        JSONObject data = JSON.parseObject(HttpUtil.sendGet(url));
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpVolumeId, String cpChapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, cpChapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[] {ThirdPart.HAI_NAN_ZHANG_YUE};
    }

    private String getSign(String... params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
        }
        return DigestUtils.md5Hex(sb.toString());
    }

}