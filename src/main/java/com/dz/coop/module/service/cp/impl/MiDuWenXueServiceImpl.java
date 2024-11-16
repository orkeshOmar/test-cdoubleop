package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.org.apache.commons.codec.digest.DigestUtils;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @project: coop-client
 * @description: 米读文学
 * @author: songwj
 * @date: 2020-06-23 20:48
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
@Slf4j
public class MiDuWenXueServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Long requestTime = System.currentTimeMillis() / 1000;
        String signStr = new StringBuilder(owchPartner.getAliasId()).append("_").append(owchPartner.getApiKey()).append("_").append(requestTime.toString()).toString();
        String sign = DigestUtils.md5Hex(signStr);
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), requestTime.toString(), sign);
        JSONArray books = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.DATA, Constant.SUCCESS_CODE_0);

        return (List<CPBook>) CollectionUtils.collect(books, obj -> {
            JSONObject book = (JSONObject) obj;
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("name"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Long requestTime = System.currentTimeMillis() / 1000;
        String signStr = new StringBuilder(owchPartner.getAliasId()).append("_").append(owchPartner.getApiKey()).append("_").append(requestTime.toString()).append("_").append(bookId).toString();
        String sign = DigestUtils.md5Hex(signStr);
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, owchPartner.getAliasId(), requestTime.toString(), sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.DATA, Constant.SUCCESS_CODE_0);
        CPBook cpBook = new CPBook();

        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("brief"));
        cpBook.setCover(data.getString("cover"));
        // 书籍连载状态，0：已完结，1：连载中
        cpBook.setCompleteStatus(StringUtils.equals(data.getString("complete_status"), "N") ? "0" : "1");
        cpBook.setCategory(data.getString("category_id"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        Long requestTime = System.currentTimeMillis() / 1000;
        String signStr = new StringBuilder(owchPartner.getAliasId()).append("_").append(owchPartner.getApiKey()).append("_").append(requestTime.toString()).append("_").append(bookId).toString();
        String sign = DigestUtils.md5Hex(signStr);
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), bookId, owchPartner.getAliasId(), requestTime.toString(), sign);
        JSONArray chapters = HttpUtil.doGet(url, JSONArray.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.DATA, Constant.SUCCESS_CODE_0);

        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(chapters, obj -> {
            JSONObject chapter = (JSONObject) obj;
            String no = chapter.getString("no");

            // 过滤书籍100000034无效章节
            if (StringUtils.equals(bookId, "100000034") && StringUtils.equals(no, "0")) {
                log.info("米读无效章节：cpBookId={},cpChapterId={},name={}", bookId, chapter.getString("id"), chapter.getString("name"));
                return null;
            }

            return new CPChapter(chapter.getString("id"), chapter.getString("name"));
        });

        collect = collect.stream().filter(cpChapter -> Objects.nonNull(cpChapter)).collect(Collectors.toList());

        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(collect);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        Long requestTime = System.currentTimeMillis() / 1000;
        String signStr = new StringBuilder(owchPartner.getAliasId()).append("_").append(owchPartner.getApiKey()).append("_").append(requestTime.toString()).append("_").append(cpBookId).append("_").append(chapterId).toString();
        String sign = DigestUtils.md5Hex(signStr);
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, owchPartner.getAliasId(), requestTime.toString(), sign);
        JSONObject data = HttpUtil.doGet(url, JSONObject.class, owchPartner, Constant.CODE, Constant.MESSAGE, Constant.DATA, Constant.SUCCESS_CODE_0);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content").replaceAll("<p>", "").replaceAll("</p>", "\n"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.MI_DU_WEN_XUE};
    }

}
