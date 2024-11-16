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
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author panqz
 * @create 2017-06-28 下午1:32
 */
@Service
public class NovelChannelServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(NovelChannelServiceImpl.class);

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, owchPartner.getAliasId(), owchPartner.getApiKey());
        String response = HttpUtil.sendGet(bookListUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Element books = root.element("books");
        Iterator<Element> iterator = books.elementIterator("book");
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String bookId = element.elementText("id");
            String bookName = element.elementText("name");
            CPBook cpBook = new CPBook();
            cpBook.setId(bookId);
            cpBook.setName(bookName);
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, owchPartner.getAliasId(), owchPartner.getApiKey(), bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.getInteger("return_code") != 0) {
            logger.error(jsonObject.getString("return_message"));
            return null;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        JSONObject book = data.getJSONObject(0);

        CPBook cpBook = new CPBook();
        cpBook.setId(bookId);
        cpBook.setName(book.getString("BookName"));
        cpBook.setAuthor(book.getString("AuthorName"));
        cpBook.setBrief(book.getString("Description"));
        cpBook.setCover(book.getString("ImageUrl"));
        cpBook.setCategory(book.getString("CategoryName"));// 所属分类
        cpBook.setCompleteStatus(book.getString("BookStatus"));
        return cpBook;


    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> cpVolumes = new ArrayList<>();

        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, owchPartner.getAliasId(), owchPartner.getApiKey(), bookId);
        String response = HttpUtil.sendGet(chapterListUrl);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.getInteger("return_code") != 0) {
            logger.error(jsonObject.getString("return_message"));
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray entry = data.getJSONArray("Entry");
        Map<String, List<CPChapter>> map = new HashMap<>();
        Map<String, String> nameMap = new HashMap<>();
        for (int i = 0; i < entry.size(); i++) {
            JSONObject chapter = entry.getJSONObject(i);
            String volumeId = chapter.getString("VolumeId");
            String volumeName = chapter.getString("VolumeName");
            if (StringUtils.isBlank(volumeId)) {
                volumeId = "1";
            }
            String name = nameMap.get(volumeId);
            if (StringUtils.isBlank(name)) {
                if (StringUtils.isBlank(volumeId)) {
                    volumeName = "正文";
                }
                nameMap.put(volumeId, volumeName);
            }
            List<CPChapter> cpChapters = map.get(volumeId);
            if (cpChapters == null) {
                cpChapters = new ArrayList<>();
            }
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(chapter.getString("ChapterId"));
            cpChapter.setName(chapter.getString("chapterName"));
            cpChapters.add(cpChapter);
            map.put(volumeId, cpChapters);
        }
        Set<Map.Entry<String, List<CPChapter>>> set = map.entrySet();
        Iterator<Map.Entry<String, List<CPChapter>>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<CPChapter>> entry1 = iterator.next();
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(entry1.getKey());
            cpVolume.setName(nameMap.get(entry1.getKey()));
            cpVolume.setChapterList(entry1.getValue());
            cpVolumes.add(cpVolume);
        }
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterinfoUrl = owchPartner.getChapterInfoUrl();
        chapterinfoUrl = MessageFormat.format(chapterinfoUrl, owchPartner.getAliasId().toString(), owchPartner.getApiKey(), chapterId);
        String response = HttpUtil.sendGet(chapterinfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(root.elementText("ChapterId"));
        cpChapter.setContent(root.elementText("ChapterContent"));
        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIAO_SHUO_PIN_DAO};
    }
}
