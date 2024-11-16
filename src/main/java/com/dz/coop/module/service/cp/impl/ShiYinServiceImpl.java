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
import java.util.*;


/**
 * @author panqz 2017-11-22 下午6:11
 */
@Deprecated
//@Service
public class ShiYinServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Long timestamp = System.currentTimeMillis();
        JSONArray books = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), timestamp.toString(), singWithSha(timestamp, owchPartner.getApiKey())), JSONArray.class);
        if (books == null || books.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("name"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        Long timestamp = System.currentTimeMillis();
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, owchPartner.getAliasId(), timestamp.toString(), singWithSha(timestamp, owchPartner.getApiKey())), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("user_name"));
        cpBook.setBrief(data.getString("desc"));
        cpBook.setCover(data.getString("img"));
        cpBook.setCompleteStatus("1".equals(data.getString("serial_status")) ? "0" : "1");

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String[] urls = owchPartner.getChapterListUrl().split(";");

        Long timestamp = System.currentTimeMillis();
        JSONArray volumes = sendGet(MessageFormat.format(urls[0], bookId, owchPartner.getAliasId(), timestamp.toString(), singWithSha(timestamp, owchPartner.getApiKey())), JSONArray.class);
        if (volumes == null || volumes.size() == 0) {
            return Collections.emptyList();
        }
        Map<String, String> volumeMap = new HashMap<>(volumes.size());
        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volume = volumes.getJSONObject(i);
            volumeMap.put(volume.getString("id"), volume.getString("name"));
        }

        timestamp = System.currentTimeMillis();
        JSONArray chapters = sendGet(MessageFormat.format(urls[1], bookId, owchPartner.getAliasId(), timestamp.toString(), singWithSha(timestamp, owchPartner.getApiKey())), JSONArray.class);
        if (chapters == null || chapters.size() == 0) {
            return Collections.emptyList();
        }

        Map<String, CPVolume> volumeMap1 = new TreeMap<>();
        for (int i = 0; i < chapters.size(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            String volumeId = chapter.getString("volume_id");
            String volumeName = volumeMap.get(volumeId);
            if (StringUtils.isBlank(volumeName)) {
                return Collections.emptyList();
            }
            CPVolume cpVolume = volumeMap1.get(volumeId);
            if (cpVolume == null) {
                cpVolume = new CPVolume();
                cpVolume.setId(volumeId);
                cpVolume.setName(volumeName);
                volumeMap1.put(volumeId, cpVolume);
            }
            List<CPChapter> cpChapters = cpVolume.getChapterList();
            if (cpChapters == null) {
                cpChapters = new ArrayList<>();
                cpVolume.setChapterList(cpChapters);
            }

            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("id"));
            cpChapter.setName(chapter.getString("name"));
            cpChapters.add(cpChapter);

        }

        List<CPVolume> cpVolumes = new ArrayList<>(volumeMap1.size());
        cpVolumes.addAll(volumeMap1.values());
        Collections.sort(cpVolumes, new Comparator<CPVolume>() {
            @Override
            public int compare(CPVolume o1, CPVolume o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }
        });

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        Long timestamp = System.currentTimeMillis();
        JSONObject data = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, owchPartner.getAliasId(), timestamp.toString(), singWithSha(timestamp, owchPartner.getApiKey())), JSONObject.class);

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
        return new ThirdPart[]{ThirdPart.SHI_YIN};
    }

    private <T> T sendGet(String url, Class<T> clazz) {

        JSONObject object = JSON.parseObject(HttpUtil.sendGet(url));
        if (!"200".equals(object.getString("code"))) {
            throw new RuntimeException("接口通讯失败，status = " + object.getString("code") + ",msg = " + object.getString("message"));
        }
        return object.getObject("data", clazz);
    }

    private String singWithSha(Long timeStamp, String key) {
        return DigestUtils.sha1Hex(key + "_" + timeStamp).toUpperCase();
    }


}
