package com.dz.coop.module.service.cp.impl;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author panqz
 * @create 2017-06-22 上午11:19
 */
@Service
public class JingYuServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(JingYuServiceImpl.class);


    private static final String RATE_190 = "rate_190";


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String response = HttpUtil.sendGet(owchPartner.getBookListUrl());
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Element items = root.element("books");
        Iterator<Element> iterator = items.elementIterator("book");
        while (iterator.hasNext()) {
            CPBook cpBook = new CPBook();
            Element item = iterator.next();
            cpBook.setId(item.elementText("id"));
            cpBook.setName(item.elementText("name"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element info = document.getRootElement();
        //Element info = root.element("book");
        CPBook cpBook = new CPBook();
        cpBook.setId(info.elementText("id"));
        cpBook.setName(info.elementText("name"));
        cpBook.setAuthor(info.elementText("author"));
        cpBook.setBrief(info.elementText("bookintr"));
        cpBook.setCover(info.elementText("smallimg"));
        cpBook.setCategory(info.elementText("class"));// 所属分类
        if ("1".equals(info.elementText("status"))) {
            cpBook.setCompleteStatus("0");
        } else if ("2".equals(info.elementText("status"))) {
            cpBook.setCompleteStatus("1");
        }
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> cpVolumes = new ArrayList<>();
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, bookId);
        String response = HttpUtil.sendGet(chapterListUrl);
        Document document = DocumentHelper.parseText(response);
        Element items = document.getRootElement();
        CPVolume cpVolume = null;

        Iterator<Element> iterator = items.elementIterator("volume");
        while (iterator.hasNext()) {
            // 解析卷
            Element volume = iterator.next();
            if (StringUtils.isBlank(volume.elementText("volumeid"))) {
                cpVolume = new CPVolume("1", "正文");
            } else {
                cpVolume = new CPVolume(volume.elementText("volumeid"), volume.elementText("volumename"));
            }

            // 解析章节
            List<CPChapter> chapterlist = new ArrayList<>();
            Iterator<Element> chapitor = volume.element("chapters").elementIterator("chap");
            while (chapitor.hasNext()) {
                Element chap = chapitor.next();
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chap.elementText("id"));
                cpChapter.setName(chap.elementText("title"));
                chapterlist.add(cpChapter);
            }
            cpVolume.setChapterList(chapterlist);
            cpVolumes.add(cpVolume);
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterinfoUrl = owchPartner.getChapterInfoUrl();
        chapterinfoUrl = MessageFormat.format(chapterinfoUrl, cpBookId, chapterId);
        String response = HttpUtil.sendGet(chapterinfoUrl);
        if (StringUtils.isNotBlank(response)) {
            response = response.replace("<p>", "").replace("</p>", "\n");
        }
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        CPChapter cpChapter = new CPChapter();
        cpChapter.setId(root.elementText("id"));
        cpChapter.setName(root.elementText("title"));
        cpChapter.setContent(root.elementText("content"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JING_YU};
    }



}
