package com.dz.coop.module.service.cp.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author panqz 2018-03-05 下午4:22
 */
@Service
public class HongGuoServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(HongGuoServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JSONArray books = doGet(MessageFormat.format(owchPartner.getBookListUrl(), "1500480000", sign(new HashMap<String, String>() {{
            put("state", "1500480000");
        }}, owchPartner.getApiKey())), JSONArray.class);
        if (books == null || books.isEmpty()) {
            logger.info("{}书籍列表为空", owchPartner.getName());
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (int i = 0; i < books.size(); i++) {
            JSONObject book = books.getJSONObject(i);
            cpBooks.add(new CPBook(book.getString("id"), book.getString("name")));
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, final String bookId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, "1500480000", sign(new HashMap<String, String>() {{
            put("bookid", bookId);
            put("state", "1500480000");
        }}, owchPartner.getApiKey())), JSONObject.class);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("id"));
        cpBook.setName(data.getString("name"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("summary"));
        cpBook.setCover(data.getString("cover"));
        cpBook.setCompleteStatus(data.getString("status"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, final String bookId) throws Exception {
        JSONArray data = doGet(MessageFormat.format(owchPartner.getChapterListUrl(), bookId, "1500480000", sign(new HashMap<String, String>() {{
            put("bookid", bookId);
            put("state", "1500480000");
        }}, owchPartner.getApiKey())), JSONArray.class);

        if (data == null || data.isEmpty()) {
            logger.info("{}书籍列表为空", owchPartner.getName());
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(data.size());

        for (int i = 0; i < data.size(); i++) {
            JSONObject cpChpater = data.getJSONObject(i);
            cpChapters.add(new CPChapter(cpChpater.getString("id"), cpChpater.getString("name")));

        }

        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, final String cpBookId, final String chapterId) throws Exception {
        JSONObject data = doGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, "1500480000", sign(new HashMap<String, String>() {{
            put("bookid", cpBookId);
            put("chapterid", chapterId);
            put("state", "1500480000");
        }}, owchPartner.getApiKey())), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.getString("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    private <T> T doGet(String url, Class<T> clazz) {
        String resp = HttpUtil.sendGet(url);
        JSONObject respObject = JSONObject.parseObject(resp);
        String code = respObject.getString("code");
        if (!"0".equals(code)) {
            throw new RuntimeException("红书汇通信异常,code=" + code + ",msg=" + respObject.getString("msg"));
        }
        return respObject.getObject("data", clazz);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HONG_GUO};
    }

    private String sign(Map<String, String> params, String key) {
        if (MapUtils.isEmpty(params)) {
            return null;
        }

        TreeMap<String, String> keys = new TreeMap<>(params);
        Set<Map.Entry<String, String>> entries = keys.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();

        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        sb.append("key=").append(key);

        return DigestUtils.md5Hex(sb.toString());
    }
}
