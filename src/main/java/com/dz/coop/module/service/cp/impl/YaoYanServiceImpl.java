package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.JavaType;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class YaoYanServiceImpl implements ClientService {
    private static final Logger log = Logger.getLogger(YaoYanServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, CPBook.class);

        String url = owchPartner.getBookListUrl();
        url = MessageFormat.format(url, owchPartner.getApiKey());
        String resultStr = HttpUtil.sendGet(url);
        JSONObject json = JsonUtils.fromJSON(resultStr, JSONObject.class);
        if (json.getInteger("code") != 0) {
            throw new Exception("获取书籍列表失败 !!" + json.getString("message"));
        }
        List<CPBook> list = JsonUtils.fromJson(json.getJSONArray("result").toJSONString(), javaType);

        return list;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String url = owchPartner.getBookInfoUrl();
        url = MessageFormat.format(url, owchPartner.getApiKey(), bookId);
        String resultStr = HttpUtil.sendGet(url);
        JSONObject json = JsonUtils.fromJSON(resultStr, JSONObject.class);
        if (json.getInteger("code") != 0) {
            throw new Exception("获取书籍详情失败 !!" + json.getString("message"));
        }
        JSONObject result = json.getJSONObject("result");

        CPBook book = new CPBook();
        book.setId(result.getString("id"));
        book.setName(result.getString("name"));
        book.setBrief(result.getString("brief"));
        book.setCover(result.getString("cover"));
        book.setAuthor(result.getString("author"));
        book.setCompleteStatus(result.getString("complete_status"));
        book.setCategory(result.getString("category"));
        return book;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, CPVolume.class);

        String url = owchPartner.getChapterListUrl();
        url = MessageFormat.format(url, owchPartner.getApiKey(), bookId);
        String result = HttpUtil.sendGet(url);
        JSONObject json = JsonUtils.fromJSON(result, JSONObject.class);
        if (json.getInteger("code") != 0) {
            throw new Exception("获取书籍章节列表失败 !!" + json.getString("message"));
        }
        JSONArray chapters = json.getJSONArray("result");
        JSONObject volumn = chapters.getJSONObject(0);
        volumn.put("id ", 1);
        volumn.put("name ", "正文");
        chapters.set(0, volumn);
        List<CPVolume> volumes = new ArrayList<>();
        for (int i = 0; i < chapters.size(); i++) {
            JSONObject jsonObject = chapters.getJSONObject(i);
            CPVolume cpVolume = new CPVolume(jsonObject.getString("id"), jsonObject.getString("name"));

            JSONArray chapterlist = jsonObject.getJSONArray("chapterlist");
            List<CPChapter> cpChapters = new ArrayList<>();
            for (int j = 0; j < chapterlist.size(); j++) {
                JSONObject jsonObject1 = chapterlist.getJSONObject(j);
                cpChapters.add(new CPChapter(jsonObject1.getString("id"), jsonObject1.getString("name")));
            }

            cpVolume.setChapterList(cpChapters);

            volumes.add(cpVolume);

        }

        return volumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        String url = owchPartner.getChapterInfoUrl();
        url = MessageFormat.format(url, owchPartner.getApiKey(), cpBookId, chapterId);
        String result = HttpUtil.sendGet(url);
        JSONObject json = JsonUtils.fromJSON(result, JSONObject.class);
        if (json.getInteger("code") != 0) {
            throw new Exception("获取书籍章节列表失败 !!" + json.getString("message"));
        }
        CPChapter zyChapter = JsonUtils.fromJSON(json.getJSONObject("result").toJSONString(), CPChapter.class);

        return zyChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

//    @Override
//    public Long[] getClientId() {
//        return new Long[]{SpecialPartnerEnum.CLIENT_76};
//    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YAO_YAN};
    }

}
