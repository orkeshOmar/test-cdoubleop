package com.dz.coop.module.service.cp.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.AudioTrailer;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.glory.common.error.DZException;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author panqz
 * @create 2017-06-24 下午12:08
 * 通用接口处理
 */
@Service
public class CommonCpServiceImpl implements ClientService {

    private Logger logger = LoggerFactory.getLogger(CommonCpServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        String bookListUrl = ClientFactory.getUrl(owchPartner.getId()).getBookListUrl(owchPartner);

        String resultStr = HttpUtil.sendGet(bookListUrl);
        JSONArray books = JSON.parseArray(resultStr);

        if (books == null || books.isEmpty()) {
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
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String bookInfotUrl = ClientFactory.getUrl(owchPartner.getId()).getBooKInfoUrl(owchPartner, bookId);


        String resultStr = HttpUtil.sendGet(bookInfotUrl);
        JSONObject book = JSON.parseObject(resultStr);

        CPBook cpBook = new CPBook();
        cpBook.setId(book.getString("id"));
        cpBook.setName(book.getString("name"));
        cpBook.setAuthor(book.getString("author"));
        cpBook.setBrief(book.getString("brief"));
        cpBook.setCompleteStatus(StringUtils.equals(owchPartner.getId() + "", ThirdPart.JING_YUE.getCpId().toString()) ? book.getString("is_finish") : book.getString("complete_status"));
        cpBook.setCover(book.getString("cover"));
        cpBook.setCategory(book.getString("category"));
        cpBook.setTag(book.getString("tag"));

        if (ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(owchPartner.getId())) {
            JSONArray trailers = book.getJSONArray("trailers");
            List<AudioTrailer> audioTrailers = new ArrayList<>();
            AudioTrailer audioTrailer = null;

            for (int i = 0; i < trailers.size(); i++) {
                audioTrailer = new AudioTrailer();

                JSONObject jsonObject = trailers.getJSONObject(i);
                audioTrailer.setTrailerId(jsonObject.getLong("trailerId"));
                audioTrailer.setBrief(jsonObject.getString("brief"));
                String url = jsonObject.getString("url");
                audioTrailer.setUrl(url != null ? url.substring(url.indexOf("/", 9)) : "");
                audioTrailer.setLastUtime(new Date(jsonObject.getLong("lastUtime")));

                audioTrailers.add(audioTrailer);
            }

            cpBook.setAudioTrailers(audioTrailers);
        }

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        String chapterListUrl = ClientFactory.getUrl(owchPartner.getId()).getChapterListUrl(owchPartner, bookId);

        JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, CPVolume.class);
        String resultStr = HttpUtil.sendGet(chapterListUrl);
        resultStr = StringUtils.isNotBlank(resultStr) && resultStr.startsWith("\uFEFF") ? StringUtils.replaceOnce(resultStr, "\uFEFF", "") : resultStr;
        List<CPVolume> volumeList = JsonUtils.fromJson(resultStr, javaType);

        if (volumeList == null) {
            throw new DZException(500, "cpId {} bid {} 卷为空", String.valueOf(owchPartner.getId()), bookId);
        }
        for (Iterator<CPVolume> iterator = volumeList.iterator(); iterator.hasNext();) {

            CPVolume cpVolume = iterator.next();

            // 过滤喜马拉雅、南京红薯“作品相关”卷
            boolean needFilter = (ThirdPart.XI_MA_LA_YA.getCpId().equals(owchPartner.getId()) || ThirdPart.NAN_JING_HONG_SHU.getCpId().equals(owchPartner.getId())) && StringUtils.equals(cpVolume.getName(), "作品相关");
            if (needFilter) {
                iterator.remove();
                continue;
            } else if (ThirdPart.WANG_YI.getCpId().equals(owchPartner.getId()) && StringUtils.equals(cpVolume.getName(), "作者声明")) {
                iterator.remove();
                continue;
            }

            List<CPChapter> chapterList = cpVolume.getChapterList();

            if (CollectionUtils.isEmpty(chapterList)) {
                String prefix = TraceKeyHolder.getUserKey("prefix");
                logger.warn("{}volumeId=[{}],volumeTitle=[{}]章节列表为空", StringUtils.isNotBlank(prefix) ? prefix : "[" + owchPartner.getName() + "]cpBookId=[" + bookId + "],", cpVolume.getId(), cpVolume.getName());
                iterator.remove();
                continue;
            }
        }

        return volumeList;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chpaterContentUrl = ClientFactory.getUrl(owchPartner.getId()).getChpaterContentUrl(owchPartner, cpBookId, chapterId);

        String resultStr = HttpUtil.sendGet(chpaterContentUrl);
        JSONObject chapInfo = JSON.parseObject(resultStr);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(chapInfo.getString("id"));
        cpChapter.setName(chapInfo.getString("name"));
        cpChapter.setContent(chapInfo.getString("content"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return ThirdPart.getThirdPartOfCommon();
    }

    @Override
    public List<CPCategory> getCpCategoryList(Partner owchPartner) throws Exception {
        String categoryUrl = ClientFactory.getUrl(owchPartner.getId()).getCategoryListUrl(owchPartner);
        if (Objects.isNull(categoryUrl)) {
            return null;
        }

        String resultStr = HttpUtil.sendGet(categoryUrl);
        JSONArray categoryJson = JSON.parseArray(resultStr);

        if (categoryJson == null || categoryJson.isEmpty()) {
            return null;
        }

        List<CPCategory> categorys = new ArrayList<>(categoryJson.size());

        for (int i = 0; i < categoryJson.size(); i++) {
            JSONObject book = categoryJson.getJSONObject(i);
            categorys.add(new CPCategory(book.getString("id"), book.getString("name")));
        }

        return categorys;
    }
}
