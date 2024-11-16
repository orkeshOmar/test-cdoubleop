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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoluoluo
 */
@Service
public class XuanTianServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> result = new ArrayList<>();
        String url = owchPartner.getBookListUrl() + "?apikey=" + owchPartner.getApiKey();
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("200".equals(object.getString("code"))) {
                JSONArray jsonArray = object.getJSONArray("msg");
                for (Object obj : jsonArray.toArray()) {
                    Map<String, Object> map = (Map) obj;
                    result.add(new CPBook(map.get("id") + "", (String) map.get("title")));
                }
            } else {
                throw new Exception("通信异常，error code : " + code + " , error msg : " + object.getString("error"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        CPBook cpBook = new CPBook();
        String url = owchPartner.getBookInfoUrl() + "?apikey=" + owchPartner.getApiKey() + "&bookid=" + bookId;
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("200".equals(object.getString("code"))) {
                Map<String, Object> map = (Map) object.get("msg");
                cpBook.setId(map.get("id") + "");
                cpBook.setName((String) map.get("title"));
                cpBook.setCover((String) map.get("cover"));
                cpBook.setBrief((String) map.get("description"));
                cpBook.setCompleteStatus(map.get("status") + "");
                cpBook.setAuthor((String) map.get("author"));
            } else {
                throw new Exception("通信异常，error code : " + code + " , error msg : " + object.getString("error"));
            }
            return cpBook;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }


    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> volumeList = new ArrayList<>();
        String url = owchPartner.getChapterListUrl() + "?apikey=" + owchPartner.getApiKey() + "&bookid=" + bookId;
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("200".equals(object.getString("code"))) {
                CPVolume cpVolume = new CPVolume("1", "正文");
                JSONArray jsonArray = object.getJSONArray("msg");
                List<CPChapter> chapterlist = new ArrayList<>();
                for (Object obj : jsonArray.toArray()) {
                    Map<String, Object> map = (Map) obj;
                    chapterlist.add(new CPChapter(map.get("id") + "", (String) map.get("title")));
                }
                cpVolume.setChapterList(chapterlist);
                volumeList.add(cpVolume);
            } else {
                throw new Exception("通信异常，error code : " + code + " , error msg : " + object.getString("error"));
            }
            return volumeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        CPChapter cpChapter = new CPChapter();
        String url = owchPartner.getChapterInfoUrl() + "?apikey=" + owchPartner.getApiKey() + "&bookid=" + cpBookId + "&chapterid=" + chapterId;
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("200".equals(object.getString("code"))) {
                Map<String, Object> map = (Map) object.get("msg");
                cpChapter.setId(chapterId);
                cpChapter.setName((String) map.get("title"));
                cpChapter.setContent((String) map.get("content"));
                cpChapter.setBookId(cpBookId);
            } else {
                throw new Exception("通信异常，error code : " + code + " , error msg : " + object.getString("error"));
            }
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
        return new ThirdPart[]{ThirdPart.XUAN_TIAN_WEN_XUE};
    }

}
