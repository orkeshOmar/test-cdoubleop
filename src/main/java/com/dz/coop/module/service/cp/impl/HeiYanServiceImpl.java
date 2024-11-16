package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.template.Template;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz
 * @create 2017-06-28 上午10:42
 */
@Service
public class HeiYanServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(HeiYanServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> bookList = new ArrayList<>();
        Integer page = 1;
        String bookUrl = owchPartner.getBookListUrl() + "?pid=" + 45 + "&page=" + page + "&pageSize=" + 300;
        List<CPBook> zyBookList = getCPBookList(bookUrl);
        if (zyBookList != null && zyBookList.size() > 0)
            bookList.addAll(zyBookList);

        while (zyBookList != null && zyBookList.size() > 1) {
            page++;
            bookUrl = owchPartner.getBookListUrl() + "?pid=45&page=" + page + "&pageSize=" + 300;
            zyBookList = getCPBookList(bookUrl);
            if (zyBookList != null && zyBookList.size() > 1)
                bookList.addAll(zyBookList);
        }
        return bookList;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        CPBook zyBook = getCPBookInfo(owchPartner.getBookInfoUrl() + "?pid=" + 45 + "&bookId=" + bookId);
        return zyBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String result = HttpUtil.sendGet(owchPartner.getChapterListUrl() + "?pid=" + 45 + "&bookId=" + bookId);
        JSONArray volumes = JSON.parseArray(result);
        if (volumes == null || volumes.isEmpty()) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>(volumes.size());
        for (int i = 0; i < volumes.size(); i++) {
            JSONObject volumesJSONObject = volumes.getJSONObject(i);
            CPVolume cpVolume = new CPVolume();
            String volumeId = volumesJSONObject.getString("id");
            String volumeTitle = volumesJSONObject.getString("name");
            cpVolume.setId(volumeId);
            cpVolume.setName(volumeTitle);

            JSONArray chapterlist = volumesJSONObject.getJSONArray("chapterlist");
            if (chapterlist == null || chapterlist.isEmpty()) {
                log.warn(Template.EMPTY_CHAPTER_LOG, owchPartner.getName(), bookId, volumeId, volumeTitle);
                continue;
                //throw new RuntimeException("章节列表为空");
            }

            for (int j = 0; j < chapterlist.size(); j++) {
                JSONObject jsonObject = chapterlist.getJSONObject(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(jsonObject.getString("id"));
                cpChapter.setName(jsonObject.getString("name"));
                cpVolume.add(cpChapter);
            }

            cpVolumes.add(cpVolume);

        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String verify = "317859";
        String heiYanSign = DigestUtils.md5Hex(chapterId + "#" + 45 + "#" + owchPartner.getApiKey());
        String sign = DigestUtils.md5Hex(verify + "#" + heiYanSign);
        CPChapter chapter = getCPChapterInfo(owchPartner.getChapterInfoUrl() + "?pid=" + 45 + "&bookId=" + cpBookId + "&chapterId=" + chapterId + "&sign=" + sign + "&verify=" + verify);
        return chapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HEI_YAN};
    }

    public List<CPBook> getCPBookList(String url) {
        JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, CPBook.class);
        List<CPBook> list = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            list = JsonUtils.fromJson(resultStr, javaType);
        } catch (Throwable e) {
            return null;
        }
        return list;
    }

    public CPBook getCPBookInfo(String url) {
        CPBook book = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            JSONObject jsonObject = JSON.parseObject(resultStr);
            book = new CPBook();
            book.setId(jsonObject.getString("id"));
            book.setName(jsonObject.getString("name"));
            book.setBrief(jsonObject.getString("brief"));
            book.setCover(jsonObject.getString("cover"));
            book.setAuthor(jsonObject.getString("author"));
            book.setCompleteStatus(jsonObject.getString("complete_status"));
            book.setCategory(jsonObject.getString("category"));
        } catch (Throwable e) {
            return null;
        }
        return book;
    }

    public CPChapter getCPChapterInfo(String url) {
        CPChapter zyChapter = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            zyChapter = JsonUtils.fromJSON(resultStr, CPChapter.class);
        } catch (Throwable e) {
            return null;
        }
        return zyChapter;
    }

}
