package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.SignUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author panqz 2017-09-01 上午11:08
 */
@Service
public class SeventeenKServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(SeventeenKServiceImpl.class);

    private static final String KEY = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEApQhuzw95wtlSNpTdb9FRcdDXfSJefsJCf9LMalmT+txJPN7eaCHbgxEmpqjZzYUDtHLeur5djQMCm0LgcXLtnwIDAQABAkEAhpiMFx/eBIZNwOpEd0/WBud2+D2xp9wzdCCuSwZn5C25sbge4a/ydVCR3IuKgyV1AEtnpcPxeWR7QA1pZ2m3gQIhANRPiPBU3KGwI4Z/lh/nqSqne4dqoc5AbhB7PP0whME/AiEAxv5R+JC0PJr7TvSOXg9ulqtG8XaBn1GIzlq4YG28W6ECIQCCGOah1m6ISaXLQh1dGZjIwW5psYbLGyIWyV27DGMdAwIhAJ3NYi2ueu+wq2ficd/PdxBOhPTI2GQCIW4a7Smy+/LhAiAFgMwTt2QpKnHsBOiYzaik02SsLzWKKk5TTpkkbzAmww==";


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        return getCpBooks(null, owchPartner.getBookListUrl(), owchPartner.getAliasId(), null, 1);

    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        sleep();

        List<CPBook> cpBooks = getBookList(owchPartner);
        if (CollectionUtils.isEmpty(cpBooks)) {
            return null;
        }
        for (CPBook cpBook : cpBooks) {
            if (cpBook.getId().equals(bookId))
                return cpBook;
        }
        return null;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        sleep();

        Map<String, String> map = new HashMap<>();
        map.put("partnerId", owchPartner.getAliasId());
        map.put("bookId", bookId);

        JSONArray content = doPost(map, owchPartner.getChapterListUrl(), JSONArray.class);
        if (null == content || content.size() == 0) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            JSONObject volume = content.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(volume.getString("volumeId"));
            cpVolume.setName(volume.getString("volumeName"));
            cpVolumes.add(cpVolume);

            JSONArray chapterList = volume.getJSONArray("chapterList");
            if (null == chapterList || chapterList.size() == 0) {
                continue;
            }

            int size = chapterList.size();
            List<CPChapter> chapters = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                JSONObject chapter = chapterList.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.getInteger("chapterId") + "");
                cpChapter.setName(chapter.getString("chapterName"));
                chapters.add(cpChapter);
            }
            cpVolume.setChapterList(chapters);
        }


        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        sleep();

        Map<String, String> map = new HashMap<>();
        map.put("partnerId", owchPartner.getAliasId());
        map.put("chapterId", chapterId);


        JSONObject content = doPost(map, owchPartner.getChapterInfoUrl(), JSONObject.class);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(content.getString("chapterContent").replaceAll("<br><br>", "\n\r"));

        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.CHUAN_MEI_CHUANG_XIANG};
    }

    private List<CPBook> getCpBooks(Map<String, String> params, String url, String cientId, List<CPBook> cpBooks, int j) throws Exception {

        if (params == null) {
            params = new HashMap<>();
            params.put("partnerId", cientId);
            params.put("lastUpdateDate", "2017-01-01 00:00:00");
        }

        if (cpBooks == null) {
            cpBooks = new ArrayList<>();
        }

        params.put("page", j + "");

        JSONObject data = doPost(params, url);


        JSONArray content = data.getJSONArray("content");
        if (content != null && content.size() > 0) {
            for (int i = 0; i < content.size(); i++) {
                JSONObject book = content.getJSONObject(i);
                CPBook cpBook = new CPBook();
                cpBook.setId(book.getString("bookId"));
                cpBook.setName(book.getString("bookName"));
                cpBook.setAuthor(book.getString("authorPenname"));
                cpBook.setBrief(book.getString("introduction"));
                cpBook.setCover(book.getString("coverImageUrl"));
                if ("01".equals(book.getString("bookStatus")) || "02".equals(data.getString("bookStatus"))) {
                    cpBook.setCompleteStatus("0");
                } else {
                    cpBook.setCompleteStatus("1");
                }
                cpBooks.add(cpBook);
            }
        }

        if (data.getInteger("hasNext") == 1) {
            getCpBooks(params, url, cientId, cpBooks, ++j);
        }

        return cpBooks;
    }

    private JSONObject doPost(Map<String, String> data, String url) throws Exception {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.putAll(data);
        dataMap.put("sign", SignUtil.sign(data, KEY));


        String response = HttpUtil.doPost(url, dataMap);
        JSONObject object = JSON.parseObject(response);
        if (!"200".equals(object.getString("code"))) {
            throw new Exception("error code = " + object.getString("code") + ",emsg = " + object.getString("msg"));
        }

        return object;
    }


    private <T> T doPost(Map<String, String> data, String url, Class<T> clazz) throws Exception {
        return doPost(data, url).getObject("content", clazz);
    }


    private void sleep() {
        try {
            Thread.sleep(1000);
        }catch(Exception e) {
            logger.error(e.getMessage() , e);
        }
    }


}
