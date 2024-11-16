package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.url.UrlService;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 小红花阅读
 * @author luozukai
 */
@Service
public class XiaoHongServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(XiaoHongServiceImpl.class);


    private UrlService getUrlHandler(Partner owchPartner) {
        return ClientFactory.getUrl(owchPartner.getId());
    }



    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String url = getUrlHandler(owchPartner).getBookListUrl(owchPartner);
        String resultStr = HttpUtil.sendGet(url);

        try {
            JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, CPBook.class);
            return JsonUtils.fromJson(resultStr, javaType);
        } catch (Exception e) {
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String url = getUrlHandler(owchPartner).getBooKInfoUrl(owchPartner, bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            CPBook book = new CPBook();
            book.setId(jsonObject.getString("id"));
            book.setName(jsonObject.getString("name"));
            book.setBrief(jsonObject.getString("brief"));
            book.setCover(jsonObject.getString("cover"));
            book.setAuthor(jsonObject.getString("author"));
            book.setCompleteStatus(jsonObject.getString("complete_status"));
            book.setCategory(jsonObject.getString("category"));
            return book;
        } catch (Exception e) {
            throw new Exception("书籍列表转换失败，错误的转换字符串 : \n" + resultStr);
        }
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> volumeList = new ArrayList<>();
        String url = getUrlHandler(owchPartner).getChapterListUrl(owchPartner, bookId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONArray jsonArray= JSON.parseArray(resultStr);
            CPVolume cpVolume = new CPVolume("1","正文");
            List<CPChapter> chapterlist = new ArrayList<>();
            for (Object obj : jsonArray.toArray()) {
                Map<String ,Object> map = (Map)obj;
                chapterlist.add(new CPChapter(map.get("id")+"",(String) map.get("name")));
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
        String url = getUrlHandler(owchPartner).getChpaterContentUrl(owchPartner, cpBookId, chapterId);
        String resultStr = HttpUtil.sendGet(url);
        try {
            return JsonUtils.fromJSON(resultStr, CPChapter.class);
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
        return new ThirdPart[]{ThirdPart.HONG_HUA};
    }

}
