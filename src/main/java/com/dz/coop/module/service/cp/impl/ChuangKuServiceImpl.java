package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz
 * @create 2017-07-20 下午3:15
 */
@Service
public class ChuangKuServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Element root = getRootElement(owchPartner.getBookListUrl());
        List<Element> books = root.elements("book");
        if (CollectionUtils.isEmpty(books)) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>();
        for (Element book : books) {
            CPBook cpBook = new CPBook();
            cpBook.setId(book.elementText("id"));
            cpBook.setName(book.elementText("booktitle"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, bookId);
        Element book = getRootElement(bookInfoUrl);
        CPBook cpBook = new CPBook();
        cpBook.setId(book.elementText("id"));
        cpBook.setName(book.elementText("title"));
        cpBook.setAuthor(book.elementText("author"));
        cpBook.setBrief(book.elementText("summary"));
        cpBook.setCover(book.elementText("cover"));
        cpBook.setCategory(book.elementText("category"));// 所属分类
        cpBook.setCompleteStatus(book.elementText("isFull"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, bookId);
        Element root = getRootElement(chapterListUrl);
        List<Element> chapters = root.elements("chapter");
        if (CollectionUtils.isEmpty(chapters)) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>();
        for (Element element : chapters) {
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(element.elementText("id"));
            cpChapter.setName(element.elementText("title"));
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterInfoUrl = owchPartner.getChapterInfoUrl();
        chapterInfoUrl = MessageFormat.format(chapterInfoUrl, cpBookId, chapterId);
        Element root = getRootElement(chapterInfoUrl);
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(root.getText());
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.CHUANG_KU};
    }

    private Element getRootElement(String url) throws Exception {
        String resp = HttpUtil.sendGet(url);
        Document document = DocumentHelper.parseText(resp);
        return document.getRootElement();
    }
}
