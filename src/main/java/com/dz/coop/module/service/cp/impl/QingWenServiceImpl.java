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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author panqz 2017-11-13 下午2:17
 */
@Service
public class QingWenServiceImpl implements ClientService {


    private final static String LIMIT = "9999999";
    private final static Integer OFFSET = 0;


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Map<String, String> paras = new HashMap<>();
        String clineId = owchPartner.getAliasId();
        paras.put("client_id", clineId);
        String timeStamp = System.currentTimeMillis() + "";
        paras.put("request_time", timeStamp);

        JSONArray ressult = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), clineId, timeStamp, sign(paras, owchPartner.getApiKey()), OFFSET, LIMIT));
        if (ressult == null || ressult.size() == 0) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(ressult.size());

        for (int i = 0; i < ressult.size(); i++) {
            JSONObject book = ressult.getJSONObject(i);
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getString("id"));
            cpBook.setName(book.getString("title"));
            cpBook.setAuthor(book.getString("author_name"));
            cpBook.setBrief(book.getString("intro"));
            cpBook.setCover(book.getString("cover"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Map<String, CPBook> bookMap = BookHolder.get();
        if (MapUtils.isEmpty(bookMap)) {
            List<CPBook> cpBooks = getBookList(owchPartner);
            if (CollectionUtils.isNotEmpty(cpBooks)) {
                bookMap = new HashMap<>(cpBooks.size());
                for (CPBook cpBook : cpBooks) {
                    bookMap.put(cpBook.getId(), cpBook);
                }
                BookHolder.set(bookMap);
            }
        }
        return bookMap.get(bookId);
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        Map<String, String> paras = new HashMap<>();
        String clineId = owchPartner.getAliasId();
        paras.put("client_id", clineId);
        String timeStamp = System.currentTimeMillis() + "";
        paras.put("request_time", timeStamp);
        paras.put("bid", bookId);

        String[] urls = owchPartner.getChapterListUrl().split(";");

        JSONArray volumeList = sendGet(MessageFormat.format(urls[0], clineId, timeStamp, bookId, sign(paras, owchPartner.getApiKey()), OFFSET, LIMIT));
        if (volumeList == null || volumeList.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>(volumeList.size());
        for (int i = 0; i < volumeList.size(); i++) {
            JSONObject volume = volumeList.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            String volumeId = volume.getString("id");
            cpVolume.setId(volumeId);
            cpVolume.setName(volume.getString("title"));
            cpVolumes.add(cpVolume);
            paras.put("vid", volumeId);
            JSONArray chapters = sendGet(MessageFormat.format(urls[1], clineId, timeStamp, bookId, volumeId, sign(paras, owchPartner.getApiKey()), OFFSET, LIMIT));
            if (chapters == null || chapters.size() == 0) {
                continue;
            }
            List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
            for (int j = 0; j < chapters.size(); j++) {
                JSONObject cpchapter = chapters.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(cpchapter.getString("id"));
                cpChapter.setName(cpchapter.getString("title"));
                cpChapters.add(cpChapter);
            }
            cpVolume.setChapterList(cpChapters);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        Map<String, String> paras = new HashMap<>();
        String clineId = owchPartner.getAliasId();
        paras.put("client_id", clineId);
        String timeStamp = System.currentTimeMillis() + "";
        paras.put("request_time", timeStamp);
        paras.put("bid", cpBookId);
        paras.put("vid", volumeId);
        paras.put("cid", chapterId);

        JSONArray chapterInfos = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), clineId, timeStamp, cpBookId, volumeId, chapterId, sign(paras, owchPartner.getApiKey())));
        for (int i = 0; i < chapterInfos.size(); i++) {
            JSONObject chapter = chapterInfos.getJSONObject(i);
            if (StringUtils.equals("0", chapter.getString("type"))) {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setContent(chapter.getString("value"));
                return cpChapter;
            }
        }

        return null;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        return null;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QING_WEN};
    }

    private JSONArray sendGet(String url) {
        return JSON.parseObject(HttpUtil.sendGet(url)).getJSONArray("results");
    }

    private String sign(Map<String, String> paras, String key) {
        TreeMap<String, String> treeMap = new TreeMap<>(paras);
        Iterator<Map.Entry<String, String>> iterator = treeMap.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().getValue()).append("#");
        }
        return DigestUtils.md5Hex(sb.append(key).toString()).toLowerCase();
    }

    public static class BookHolder {

        private static ThreadLocal<Map<String, CPBook>> threadLocal = new ThreadLocal<>();

        public static Map<String, CPBook> get() {
            return threadLocal.get();
        }

        public static void set(Map<String, CPBook> map) {
            threadLocal.set(map);
        }

        public static void clear() {
            threadLocal.remove();
        }
    }
}
