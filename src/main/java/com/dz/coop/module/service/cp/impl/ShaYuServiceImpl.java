package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.CheckUtil;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author panqz
 * @create 2017-06-22 上午11:19
 */
@Service
public class ShaYuServiceImpl implements ClientService {

    private UrlService getUrlHandler(Partner owchPartner) {
        return ClientFactory.getUrl(owchPartner.getId());
    }

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = getUrlHandler(owchPartner).getBookListUrl(owchPartner);
        List<CPBook> result = new ArrayList<>();
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(object.getString("code"))) {
                JSONArray jsonArray = object.getJSONArray("data");
                for (Object obj : jsonArray.toArray()) {
                    Map<String ,Object> map = (Map)obj;
                    result.add(new CPBook(map.get("bookid")+"",(String)map.get("bookname")));
                }
            }else{
                throw new Exception("通信异常，error code : " + code +" , error msg : " + object.getString("error"));
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
        String url = getUrlHandler(owchPartner).getBooKInfoUrl(owchPartner,bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(object.getString("code"))) {
                Map<String ,Object> map = (Map)object.get("data");
                cpBook.setId(map.get("bookid")+"");
                cpBook.setName((String)map.get("bookname"));
                cpBook.setCover((String)map.get("bookimg"));
                cpBook.setBrief((String)map.get("bookintro"));
                cpBook.setCategory(MapUtils.getString(map,"category"));
                if(map.get("endstatus") != null){
                    if("1".equals(map.get("endstatus")+"")){
                        cpBook.setCompleteStatus("0");
                    }else if("2".equals(map.get("endstatus")+"")){
                        cpBook.setCompleteStatus("1");
                    }
                }
                cpBook.setAuthor((String)map.get("author"));
            }else{
                throw new Exception("通信异常，error code : " + code +" , error msg : " + object.getString("error"));
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
        String url = getUrlHandler(owchPartner).getChapterListUrl(owchPartner,bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(object.getString("code"))) {
                CPVolume cpVolume = new CPVolume("1","正文");
                JSONArray jsonArray = object.getJSONArray("data");
                List<CPChapter> chapterlist = new ArrayList<>();
                for (Object obj : jsonArray.toArray()) {
                    Map<String ,Object> map = (Map)obj;
                    chapterlist.add(new CPChapter(map.get("chapterid")+"",(String) map.get("chaptername")));
                }
                cpVolume.setChapterList(chapterlist);
                volumeList.add(cpVolume);
            }else{
                throw new Exception("通信异常，error code : " + code +" , error msg : " + object.getString("error"));
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
        String url = getUrlHandler(owchPartner).getChpaterContentUrl(owchPartner,cpBookId,chapterId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(object.getString("code"))) {
                Map<String ,Object> map = (Map)object.get("data");
                cpChapter.setId(chapterId);
                cpChapter.setName((String)map.get("chaptername"));
                cpChapter.setContent((String)map.get("chaptercontent"));
                cpChapter.setBookId(cpBookId);
            }else{
                throw new Exception("通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
            return cpChapter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHA_YU_ZHONG_WEN};
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public List<CPCategory> getCpCategoryList(Partner owchPartner) throws Exception {
        String url = ClientFactory.getUrl(owchPartner.getId()).getCategoryListUrl(owchPartner);
        List<CPCategory> result = new ArrayList<>();
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(code)) {
                JSONArray jsonArray = object.getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.getString("id");
                    String name = obj.getString("name");
                    CheckUtil.checkIsBlank(id, 500, "分类id为空白");
                    CheckUtil.checkIsBlank(name, 500, "分类name为空白");
                    CPCategory category = new CPCategory(id, name);
                    result.add(category);
                }
            }else{
                throw new Exception("通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
            return result;
        } catch (Exception e) {
            throw new Exception("获取分类转换失败，错误的转换字符串 : \n" + resultStr, e);
        }
    }
}
