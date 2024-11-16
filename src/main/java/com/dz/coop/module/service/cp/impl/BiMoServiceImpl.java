package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.CharacterConverterUtil;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @project: coop-client
 * @description: 笔墨接口对接
 * @author: songwj
 * @date: 2021-03-26 16:32
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BiMoServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = getBookArray(owchPartner);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(convert(book.getString("novelid")));
            cpBook.setName(book.getString("novelName"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String cpBookId) throws Exception {
        JSONArray books = getBookArray(owchPartner);
        CPBook cpBook = new CPBook();

        if (books != null && books.size() > 0) {
           for (int i = 0; i < books.size(); i++) {
               JSONObject data = books.getJSONObject(i);

               if (StringUtils.equals(convert(data.getString("novelid")), cpBookId)) {
                   cpBook.setId(convert(data.getString("novelid")));
                   cpBook.setName(data.getString("novelName"));
                   cpBook.setAuthor(data.getString("authorName"));
                   cpBook.setBrief(data.getString("summary"));
                   cpBook.setCover(data.getString("cover"));
                   // 是否完结,1完结 0连载
                   cpBook.setCompleteStatus(data.getString("isComplete"));
                   // 分类名
                   cpBook.setCategory(data.getString("categoryName"));

                   return cpBook;
               }
           }
        }

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String cpBookId) throws Exception {
        Map<String, Object> paramMap = new TreeMap<>();
        String timeMillis = String.valueOf(System.currentTimeMillis() / 1000);
        int page = 1;
        String pageSize = "3000";
        paramMap.put("time", timeMillis);
        paramMap.put("page", page);
        paramMap.put("pagesize", pageSize);
        paramMap.put("apiKey", owchPartner.getAliasId());
        String sign = getSign(paramMap, owchPartner.getApiKey());
        cpBookId = cpBookId.split("_")[0];
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), cpBookId, timeMillis, page, pageSize, owchPartner.getAliasId(), sign);

        JSONObject chapterList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);

        JSONArray chapters = getAllChapter(owchPartner, paramMap, chapterList, cpBookId, page, pageSize);

        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            return new CPChapter(convert(chapter.getString("chapterid")), chapter.getString("chapterTitle"));
        });

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(collect);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpChapterId) throws Exception {
        Map<String, Object> paramMap = new TreeMap<>();
        String timeMillis = String.valueOf(System.currentTimeMillis() / 1000);
        paramMap.put("time", timeMillis);
        paramMap.put("apiKey", owchPartner.getAliasId());
        String sign = getSign(paramMap, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId.split("_")[0], cpChapterId.split("_")[0], timeMillis, owchPartner.getAliasId(), sign);
        JSONObject chapter = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(chapter.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpVolumeId, String cpChapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, cpChapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[] {ThirdPart.BI_MO};
    }

    private JSONArray getBookArray(Partner owchPartner) throws Exception {
        Map<String, Object> paramMap = new TreeMap<>();
        String timeMillis = String.valueOf(System.currentTimeMillis() / 1000);
        paramMap.put("time", timeMillis);
        paramMap.put("apiKey", owchPartner.getAliasId());
        String sign = getSign(paramMap, owchPartner.getApiKey());
        String url = MessageFormat.format(owchPartner.getBookListUrl(), timeMillis, owchPartner.getAliasId(), sign);
        JSONObject bookList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);
        return bookList.getJSONArray(Constant.LIST);
    }

    private JSONArray getAllChapter(Partner owchPartner, Map<String, Object> paramMap, JSONObject bookList, String cpBookId, int page, String pageSize) throws Exception {
        JSONArray list = bookList.getJSONArray(Constant.LIST);
        int totalPage = bookList.getIntValue("totalPage");

        if (totalPage > page) {
            for (int i = page + 1; i <= totalPage; i++) {
                String timeMillis = String.valueOf(System.currentTimeMillis() / 1000);
                paramMap.put("time", timeMillis);
                paramMap.put("page", i);
                String sign = getSign(paramMap, owchPartner.getApiKey());
                String url = MessageFormat.format(owchPartner.getChapterListUrl(), cpBookId, timeMillis, String.valueOf(i), pageSize, owchPartner.getAliasId(), sign);
                bookList = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MSG, Constant.DATA, Constant.SUCCESS_CODE_200);
                list.addAll(bookList.getJSONArray(Constant.LIST));
            }
        }

        return list;
    }

    private String getSign(Map<String, Object> paramMap, String apiSecret) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
        for (Map.Entry me : entries) {
            sb.append(me.getKey()).append("=").append(me.getValue()).append("&");
        }
        sb.append("apiSecret=").append(apiSecret);
        return DigestUtils.md5Hex(sb.toString());
    }

    private String convert(String srcId) {
        if (StringUtils.isNotBlank(srcId)) {
            return srcId + "_" + CharacterConverterUtil.convertchar2number(srcId);
        }

        return srcId;
    }

}
