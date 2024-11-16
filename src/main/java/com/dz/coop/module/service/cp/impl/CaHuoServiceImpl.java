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
 * @author panqz 2017-10-09 下午2:10
 */
@Service
public class CaHuoServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Element root = getRootElement(MessageFormat.format(owchPartner.getBookListUrl(), "booklist", owchPartner.getApiKey()));

        List<Element> books = root.elements("book");
        if (CollectionUtils.isEmpty(books)) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (Element book : books) {
            CPBook cpBook = new CPBook();
            cpBook.setId(book.elementText("id"));
            cpBook.setCompleteStatus(book.elementText("isfinish"));
            cpBooks.add(cpBook);
        }

        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        Element root = getRootElement(MessageFormat.format(owchPartner.getBookInfoUrl(), "book", owchPartner.getApiKey(), bookId));

        CPBook cpBook = new CPBook();
        cpBook.setId(root.elementText("id"));
        cpBook.setName(root.elementText("name"));
        cpBook.setAuthor(root.elementText("author"));
        cpBook.setBrief(root.elementText("summary"));
        cpBook.setCover(root.elementText("cover"));
        cpBook.setCategory(root.elementText("category"));

        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        Element root = getRootElement(MessageFormat.format(owchPartner.getChapterListUrl(), "chapters", owchPartner.getApiKey(), bookId));

        List<Element> chapters = root.elements("chapter");
        if (CollectionUtils.isEmpty(chapters)) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        for (Element element : chapters) {
            if (isVolume(element)) {
                CPVolume cpVolume = new CPVolume();
                cpVolume.setId(element.elementText("id"));
                cpVolume.setName(element.elementText("title"));
                cpVolumes.add(cpVolume);
            } else {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(element.elementText("id"));
                cpChapter.setName(element.elementText("title"));
                if (cpVolumes.size() == 0) {
                    CPVolume cpVolume = new CPVolume();
                    cpVolume.setId("1");
                    cpVolume.setName("正文");
                    cpVolumes.add(cpVolume);
                }
                CPVolume cpVolume = cpVolumes.get(cpVolumes.size() - 1);
                List<CPChapter> cpChapters = cpVolume.getChapterList();
                if (CollectionUtils.isEmpty(cpChapters)) {
                    cpChapters = new ArrayList<>();
                    cpVolume.setChapterList(cpChapters);
                }
                cpChapters.add(cpChapter);
            }
        }

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        Element root = getRootElement(MessageFormat.format(owchPartner.getChapterInfoUrl(), "chapter", owchPartner.getApiKey(), cpBookId, chapterId));

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(root.elementText("content").replaceAll("<br/>", "\r\n"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.CA_HUO};
    }

    private Element getRootElement(String url) throws Exception {
        Document document = DocumentHelper.parseText(HttpUtil.sendGet(url));
        return document.getRootElement();
    }

    private boolean isVolume(Element element) throws Exception {
        return "1".equals(element.elementText("volumeOrder"));
    }
}
