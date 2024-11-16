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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author panqz 2018-11-22 2:19 PM
 */
@Service
public class XiaoShuoKongServiceImpl implements ClientService {


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Map<String, Object> parasm = new HashMap<>();
        parasm.put("sid", owchPartner.getAliasId());
        return recursiveGet(parasm, 1, null, owchPartner);
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Map<String, Object> parasm = new HashMap<>();
        parasm.put("sid", owchPartner.getAliasId());
        parasm.put("aid", bookId);
        String s = HttpUtil.sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), bookId, sign(parasm, owchPartner.getApiKey())));
        JSONObject data = JSONObject.parseObject(s);

        CPBook cpBook = new CPBook();
        cpBook.setId(data.getString("articleid"));
        cpBook.setName(data.getString("articlename"));
        cpBook.setAuthor(data.getString("author"));
        cpBook.setBrief(data.getString("intro"));
        cpBook.setCover(data.getString("cover"));

        cpBook.setCompleteStatus(data.getString("fullflag"));

        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        Map<String, Object> parasm = new HashMap<>();
        parasm.put("sid", owchPartner.getAliasId());
        parasm.put("aid", bookId);
        String s = HttpUtil.sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), bookId, sign(parasm, owchPartner.getApiKey())));
        JSONArray data = JSONObject.parseArray(s);
        if (data.isEmpty()) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>(1);
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");

        List<CPChapter> cpChapters = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            if (StringUtils.equals(jsonObject.getString("chaptertype"), "1")) {
                continue;
            }
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(jsonObject.getString("chapterid"));
            cpChapter.setName(jsonObject.getString("chaptername"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        Map<String, Object> parasm = new HashMap<>();
        parasm.put("sid", owchPartner.getAliasId());
        parasm.put("aid", cpBookId);
        parasm.put("cid", chapterId);
        String s = HttpUtil.sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), cpBookId, chapterId, sign(parasm, owchPartner.getApiKey())));
        JSONObject data = JSONObject.parseObject(s);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(data.getString("chapterid"));
        String content = data.getString("content");
        if (StringUtils.isNotBlank(content)) {
            content = content.replaceAll("<p>", "").replaceAll("\r", "").replaceAll("</p>", "\n\r");
        }
        cpChapter.setContent(content);

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIAO_SHUO_KONG};
    }


    public List<CPBook> recursiveGet(Map<String, Object> parasm, int page, List<CPBook> cpBooks, Partner owchPartner) {
        if (cpBooks == null) {
            cpBooks = new ArrayList<>();
        }
        parasm.put("page", page);
        String s = null;
        try {
            s = HttpUtil.sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), page, sign(parasm, owchPartner.getApiKey())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray array = JSONObject.parseArray(s);
        if (!array.isEmpty()) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject book = array.getJSONObject(i);
                CPBook cpBook = new CPBook();
                cpBook.setId(book.getString("articleid"));
                cpBooks.add(cpBook);
            }
            return recursiveGet(parasm, ++page, cpBooks, owchPartner);
        }
        return cpBooks;
    }

    private String sign(Map<String, Object> parasm, String key) throws Exception {
        Map<String, Object> treeMap = new TreeMap<>(parasm);
        Set<Map.Entry<String, Object>> entries = treeMap.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Iterator<Map.Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> next = iterator.next();
            sb.append(next.getKey()).append("=").append(next.getValue()).append("&");
        }
        return getMD5(sb.append("key=").append(key).toString());
    }

    private static String getMD5(String str) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(str.getBytes());
        byte[] md5Bytes = md5.digest();
        String res = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            int temp = md5Bytes[i] & 0xFF;
            if (temp <= 0XF) {
                res += "0";
            }
            res += Integer.toHexString(temp);
        }
        return res;
    }
}
