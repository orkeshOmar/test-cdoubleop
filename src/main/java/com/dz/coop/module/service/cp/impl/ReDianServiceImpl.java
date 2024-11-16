package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-07-09 上午9:44
 */
@Service
public class ReDianServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ReDianServiceImpl.class);


    private static final String SUCCESS_CODE = "200";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Element data = getMsgElement(MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey()));
        List<Element> books = data.elements("item");
        if (CollectionUtils.isEmpty(books)) {
            return Collections.emptyList();
        }
        List<CPBook> cpBooks = new ArrayList<>(books.size());
        for (Element element : books) {
            CPBook cpBook = new CPBook();
            cpBook.setId(element.elementText("id"));
            cpBook.setName(element.elementText("title"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Element data = getMsgElement(MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId));
        CPBook cpBook = new CPBook();
        cpBook.setId(data.elementText("id"));
        cpBook.setName(data.elementText("title"));
        cpBook.setCover(data.elementText("cover"));
        cpBook.setBrief(data.elementText("description"));
        cpBook.setCompleteStatus(StringUtils.equals(data.elementText("status"), "连载") ? "0" : "1");
        cpBook.setAuthor(data.elementText("author"));
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        Element data = getMsgElement(MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId));
        Element sectionids = data.element("sectionid");
        List<Element> chapters = sectionids.elements("item");
        if (CollectionUtils.isEmpty(chapters)) {
            return Collections.emptyList();
        }
        List<CPVolume> cpVolumes = new ArrayList<>();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("正文");
        List<CPChapter> cpChapters = new ArrayList<>(chapters.size());
        for (Element element : chapters) {
            CPChapter cpChapter = new CPChapter();
            cpChapter.setId(element.getText());
            cpChapters.add(cpChapter);
        }
        cpVolume.setChapterList(cpChapters);
        cpVolumes.add(cpVolume);
        return cpVolumes;
    }


    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        Element data = getMsgElement(MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), chapterId));
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(data.elementText("content"));
        cpChapter.setName(data.elementText("title"));
        return cpChapter;

    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.RE_DIAN};
    }

    private Element getMsgElement(String url) {
        try {
            String xml = HttpUtil.sendGet(url);
            Document document = DocumentHelper.parseText(xml);
            Element rootElement = document.getRootElement();
            if (!StringUtils.equals(rootElement.elementText("code"), SUCCESS_CODE)) {
                throw new RuntimeException("热点接口异常,code=" + rootElement.elementText("code") + ",msg=" + rootElement.elementText("msg"));
            }
            return rootElement.element("msg");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
