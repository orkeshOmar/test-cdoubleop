package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.MD5Util;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.url.UrlService;
import com.dz.coop.module.service.url.impl.GongZhuUrlServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公主书城
 * @author luozukai
 */
@Service
public class GongZhuServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(GongZhuServiceImpl.class);

    private UrlService getUrlHandler(Partner owchPartner) {
        return ClientFactory.getUrl(owchPartner.getId());
    }

    public String getBookListUrl(Partner owchPartner,int page) {
        String path = "page="+page+"&sid="+ GongZhuUrlServiceImpl.sid;
        String sign = MD5Util.MD5(path + "&key="+GongZhuUrlServiceImpl.key);
        path += "&sign="+sign;
        return owchPartner.getBookListUrl() + "?" + path;
    }

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> bookList = new ArrayList<>();
        try {
            int resultSize;
            int page = 1;
            do {
                String url = getBookListUrl(owchPartner, page);
                String resultStr = HttpUtil.sendGet(url);
                List<CPBook> currResultList = new ArrayList<>();
                JSONArray jsonArray = JSON.parseArray(resultStr);
                for (Object obj : jsonArray.toArray()) {
                    Map<String, Object> map = (Map) obj;
                    currResultList.add(new CPBook(map.get("articleid") + "", ((String) map.get("articlename")).replaceAll("【完本】", "")));
                }
                resultSize = currResultList.size();
                bookList.addAll(currResultList);
                logger.info(page + "页--->size:" + resultSize);
                page++;
            } while (resultSize >= 100);

            return bookList;
        } catch (Exception e) {
            throw new Exception("书籍列表转换失败" + e.getMessage());
        }
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        CPBook cpBook = new CPBook();
        String url = getUrlHandler(owchPartner).getBooKInfoUrl(owchPartner,bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            Map<String, Object> map = JSON.parseObject(resultStr);
            cpBook.setId(map.get("articleid") + "");
            cpBook.setName(((String) map.get("articlename")).replaceAll("【完本】", ""));
            cpBook.setCover((String) map.get("cover"));
            cpBook.setAuthor((String) map.get("author"));
            cpBook.setCompleteStatus(map.get("fullflag") + "");
            cpBook.setBrief((String) map.get("intro"));
            return cpBook;
        } catch (Exception e) {
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> volumeList = new ArrayList<>();
        String url = getUrlHandler(owchPartner).getChapterListUrl(owchPartner,bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONArray jsonArray = JSON.parseArray(resultStr);
            CPVolume cpVolume = new CPVolume("1", "正文");
            List<CPChapter> chapterlist = new ArrayList<>();
            for (Object obj : jsonArray.toArray()) {
                Map<String, Object> map = (Map) obj;
                chapterlist.add(new CPChapter(map.get("chapterid") + "", (String) map.get("chaptername")));
            }
            cpVolume.setChapterList(chapterlist);
            volumeList.add(cpVolume);
            return volumeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        CPChapter cpChapter = new CPChapter();
        String url = getUrlHandler(owchPartner).getChpaterContentUrl(owchPartner,cpBookId,chapterId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            Map<String, Object> map = JSON.parseObject(resultStr);
            cpChapter.setId(chapterId);
            cpChapter.setName((String) map.get("chaptername"));
            cpChapter.setContent((String) map.get("content"));
            cpChapter.setBookId(cpBookId);
            return cpChapter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.GONG_ZHU_SHU_CHENG};
    }

}
