package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz 2018-11-12 9:21 AM
 */
@Service
public class DingTianServiceImpl implements ClientService {
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Element books = sendGet(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey()));

        List<Element> elements = books.elements("book");
        return (List<CPBook>) CollectionUtils.collect(elements, element -> {
            CPBook cpBook = new CPBook(element.elementText("id"), element.elementText("name"));
            return cpBook;
        });
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        Element book = sendGet(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId));

        CPBook cpBook = new CPBook();
        cpBook.setId(book.elementText("id"));
        cpBook.setName(book.elementText("name"));
        cpBook.setAuthor(book.elementText("author"));
        cpBook.setBrief(book.elementText("summary"));
        cpBook.setCover(book.elementText("cover"));
        cpBook.setCompleteStatus(book.elementText("isfinish"));

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        Element chapters = sendGet(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId));

        List<Element> elements = chapters.elements("chapter");
        List<CPChapter> collect = (List<CPChapter>) CollectionUtils.collect(elements, element -> new CPChapter(element.elementText("id"), element.elementText("title")));

        List<CPVolume> cpVolumes = new ArrayList<>(1);

        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        cpVolume.setChapterList(collect);

        cpVolumes.add(cpVolume);

        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        Element book = sendGet(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId));

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(StringUtils.defaultIfBlank(book.elementText("content"), "").replaceAll("<br/>", "\n\r"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.DING_TIAN, ThirdPart.AI_YUE};
    }

    private Element sendGet(String url) throws DocumentException {
        String xml = HttpUtil.sendGet(url);
        Document document = DocumentHelper.parseText(xml);
        return document.getRootElement();
    }
}
