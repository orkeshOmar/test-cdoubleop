package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
public class ShuCongServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String response = HttpUtil.sendGet(owchPartner.getBookListUrl());
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Element items = root.element("items");
        Iterator<Element> iterator = items.elementIterator("item");
        while (iterator.hasNext()) {
            CPBook cpBook = new CPBook();
            Element item = iterator.next();
            cpBook.setId(item.elementText("bookid"));
            cpBook.setName(item.elementText("title"));
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
        Element root = document.getRootElement();
        Element info = root.element("info");
        CPBook cpBook = new CPBook();
        cpBook.setId(info.elementText("bookid"));
        cpBook.setName(info.elementText("title"));
        cpBook.setAuthor(info.elementText("author"));
        cpBook.setBrief(info.elementText("comment"));
        cpBook.setCover(info.elementText("image_small"));
        cpBook.setCategory(info.elementText("category"));// 所属分类
        cpBook.setCompleteStatus(info.elementText("fullflag"));
        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> cpVolumes = new ArrayList<>();
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, bookId);
        String response = HttpUtil.sendGet(chapterListUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Element items = root.element("items");
        Iterator<Element> iterator = items.elementIterator("item");

        CPVolume cpVolume = null;
        List<CPChapter> chapterlist = null;
        while (iterator.hasNext()) {
            Element item = iterator.next();
            String chapterType = item.elementText("chaptertype");
            if ("1".equals(chapterType)) {
                if (cpVolume == null) {
                    cpVolume = new CPVolume(item.elementText("cid"), item.elementText("chaptername"));
                } else {
                    cpVolume.setChapterList(chapterlist);
                    cpVolumes.add(cpVolume);
                    cpVolume = new CPVolume(item.elementText("cid"), item.elementText("chaptername"));
                    chapterlist = null;
                }
            } else {
                if (chapterlist == null)
                    chapterlist = new ArrayList<>();
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(item.elementText("cid"));
                cpChapter.setName(item.elementText("chaptername"));
                chapterlist.add(cpChapter);
            }
        }

        if (cpVolume == null && chapterlist != null) {
            cpVolume = new CPVolume("1", "正文");
            cpVolume.setChapterList(chapterlist);
            cpVolumes.add(cpVolume);
            return cpVolumes;
        }

        cpVolume.setChapterList(chapterlist);
        cpVolumes.add(cpVolume);
        return cpVolumes;


    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String chapterinfoUrl = owchPartner.getChapterInfoUrl();
        chapterinfoUrl = MessageFormat.format(chapterinfoUrl, cpBookId, chapterId);
        String response = HttpUtil.sendGet(chapterinfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        CPChapter cpChapter = new CPChapter();
        cpChapter.setName(root.elementText("chaptertitle"));
        cpChapter.setContent(root.elementText("chaptercontent"));
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHU_CONG};
    }
}
