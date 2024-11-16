package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ZhuJieServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ZhuJieServiceImpl.class);
    private static final String KEY = "u1l4muiw2d";
    private static final String SECRET = "r2hc1bcj6w4zr8moc4u9";

    private UrlService getUrlHandler(Partner owchPartner) {
        return ClientFactory.getUrl(owchPartner.getId());
    }

    private String contentHandler(String old){
        String result=old;
        if (old!=null){
            result=old.replace("<br/><br/>","\r\n");
        }
        return result;
    }

    private Map<String,String> getheaderMap(Partner owchPartner){
        Map<String,String> headerMap=new HashMap<>();
        long time=System.currentTimeMillis()/1000;
        String text="appid="+owchPartner.getApiKey()+"&secret="+SECRET+"&sid="+KEY+"&timestamp="+time;
        String sign = DigestUtils.md5Hex(text).toUpperCase();

        headerMap.put("appid",owchPartner.getApiKey());
        headerMap.put("sid",KEY);
        headerMap.put("secret",SECRET);
        headerMap.put("timestamp",time+"");
        headerMap.put("sign",sign);

        return headerMap;
    }


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = getUrlHandler(owchPartner).getBookListUrl(owchPartner);
        List<CPBook> result = new ArrayList<>();
        Map<String,String> jsonMap=new HashMap<>();
        jsonMap.put("startTime","2024-01-01 00:00:00");
        jsonMap.put("endTime","");
        Map<String,String> headerMap=getheaderMap(owchPartner);

        String resultStr=HttpUtil.sentPost(url,jsonMap,headerMap);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(code)) {
                String data=object.getString("data");
                if (data!=null){
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (Object obj : jsonArray.toArray()) {
                        Map<String ,Object> map = (Map)obj;
                        result.add(new CPBook(map.get("book_id")+"", (String) map.get("book_name")));
                    }
                }
            }else{
                log.error("getBookList 通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
        } catch (Exception e) {
            log.error("getBookList 转换失败，错误的转换字符串 : \n" + resultStr, e.getMessage());
        }

        return result;
    }

    private String getTag(String tag){
        String result="";
        if (tag!=null){
            String[] tags=tag.split(",");
            if (tags.length>0){
                result=tags[0];
            }
            for (String tag1:tags){
                if ("14".equals(tag1)||"15".equals(tag1)){
                    result=tag1;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        CPBook cpBook = new CPBook();
        String url = getUrlHandler(owchPartner).getBooKInfoUrl(owchPartner,bookId);
        Map<String,String> jsonMap=new HashMap<>();
        jsonMap.put("book_id",bookId);
        Map<String,String> headerMap=getheaderMap(owchPartner);

        String resultStr=HttpUtil.sentPost(url,jsonMap,headerMap);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            if ((int)object.get("code")==0){
                Map map = (Map)object.get("data");
                cpBook.setId(map.get("id")+"");
                cpBook.setName((String)map.get("title"));
                cpBook.setCover((String)map.get("cover"));
                cpBook.setBrief((String)map.get("description"));
                cpBook.setCategory(getTag((String) map.get("tags")));
                cpBook.setAuthor("佚名");
                cpBook.setCompleteStatus("1");
            }else{
                log.error("getBookInfo 通信异常，error code : " + object.get("code") +" , error msg : " + object.getString("error"));
            }
        } catch (Exception e) {
            log.error("getBookInfo 转换失败，错误的转换字符串 : \n" + resultStr, e.getMessage());
        }

        return cpBook;
    }


    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> list=new ArrayList<>();
        List<CPChapter> chapterList = new ArrayList<>();
        String url = getUrlHandler(owchPartner).getChpaterContentUrl(owchPartner,bookId,null);
        Map<String,String> jsonMap=new HashMap<>();
        jsonMap.put("book_id",bookId);
        Map<String,String> headerMap=getheaderMap(owchPartner);

        String resultStr=HttpUtil.sentPost(url,jsonMap,headerMap);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(code)) {
                JSONArray jsonArray=object.getJSONArray("data");
                for (Object data : jsonArray){
                    Map map = (Map)data;
                    CPChapter cpChapter=new CPChapter();
                    cpChapter.setId(String.valueOf(map.get("id")));
                    cpChapter.setName((String) map.get("name"));
                    cpChapter.setContent(contentHandler((String) map.get("content")));
                    cpChapter.setBookId(bookId);
                    cpChapter.setIsFree((int)map.get("free"));
                    cpChapter.setLastUtime((String) map.get("updated_at"));
                    chapterList.add(cpChapter);
                }

                CPVolume cpVolume=new CPVolume();
                cpVolume.setId("1");
                cpVolume.setName("正文");
                cpVolume.setBookId(bookId);
                cpVolume.setChapterList(chapterList);
                list.add(cpVolume);
            }else{
                log.error("getCPChapterInfo 通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
        } catch (Exception e) {
            log.error("getCPChapterInfo 转换失败，错误的转换字符串 : \n" + resultStr, e.getMessage());
        }
        return list;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        CPChapter cpChapter = new CPChapter();
        String url = getUrlHandler(owchPartner).getChpaterContentUrl(owchPartner,cpBookId,chapterId);
        Map<String,String> jsonMap=new HashMap<>();
        jsonMap.put("book_id",cpBookId);
        Map<String,String> headerMap=getheaderMap(owchPartner);

        String resultStr=HttpUtil.sentPost(url,jsonMap,headerMap);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(code)) {
                JSONArray jsonArray=object.getJSONArray("data");
                for (Object data : jsonArray){
                    Map map = (Map)data;
                    if (String.valueOf(map.get("id")).equals(chapterId)){
                        cpChapter.setId(String.valueOf(map.get("id")));
                        cpChapter.setName((String)map.get("name"));
                        cpChapter.setContent(contentHandler((String) map.get("content")));
                        cpChapter.setBookId(cpBookId);
                        cpChapter.setIsFree((int)map.get("free"));
                        cpChapter.setLastUtime((String) map.get("updated_at"));
                    }
                }
            }else{
                log.error("getCPChapterInfo 通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
        } catch (Exception e) {
            log.error("getCPChapterInfo 转换失败，错误的转换字符串 : \n" + resultStr, e.getMessage());
        }
        return cpChapter;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHU_JIE_DUAN_PIAN};
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public List<CPCategory> getCpCategoryList(Partner owchPartner) throws Exception {
        List<CPCategory> list=new ArrayList<>();
        String url = getUrlHandler(owchPartner).getCategoryListUrl(owchPartner);
        Map<String,String> jsonMap=new HashMap<>();
        Map<String,String> headerMap=getheaderMap(owchPartner);

        String resultStr=HttpUtil.sentPost(url,jsonMap,headerMap);
        try {
            JSONObject object = JSONObject.parseObject(resultStr);
            String code = object.getString("code");
            if ("0".equals(code)) {
                JSONArray jsonArray=object.getJSONArray("data");
                for (Object data : jsonArray){
                    Map map = (Map)data;
                    Integer id=(Integer)map.get("id");
                    String tagName=(String)map.get("tag_name");
                    CPCategory cpCategory=new CPCategory(String.valueOf(id),tagName);
                    list.add(cpCategory);
                }
            }else{
                log.error("getCpCategoryList 通信异常，error code : " + code +" , error msg : " + object.getString("error"));
            }
        }catch (Exception e){
            log.error("getCpCategoryList 转换失败，错误的转换字符串 : \n" + resultStr, e.getMessage());
        }

        return list;
    }

}
