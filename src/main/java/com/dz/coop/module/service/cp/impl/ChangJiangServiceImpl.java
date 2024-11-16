package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.model.vo.*;
import com.dz.coop.module.service.cp.ClientService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqz
 * @create 2017-03-24 下午2:45
 */
@Service
public class ChangJiangServiceImpl implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ChangJiangServiceImpl.class);
    private static final String CHANG_JIANG_MAC = "dianzhong";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String key = owchPartner.getApiKey();
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, CHANG_JIANG_MAC, key);
        String response = HttpUtil.sendGet(bookListUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("datas", ChangJiangBooks.class);
        xstream.alias("item", ChangJiangBook.class);
        xstream.aliasField("id", ChangJiangBook.class, "id");
        xstream.aliasField("bookname", ChangJiangBook.class, "bookName");
        xstream.addImplicitCollection(ChangJiangBooks.class, "books");
        ChangJiangBooks changJiangBooks = (ChangJiangBooks) xstream.fromXML(response);
        if (changJiangBooks == null) {
            throw new Exception("书籍列表为空");
        }
        List<ChangJiangBook> books = changJiangBooks.getBooks();
        if (CollectionUtils.isEmpty(books)) {
            throw new Exception("书籍列表为空");
        }
        CPBook cpBook;
        for (ChangJiangBook book : books) {
            cpBook = new CPBook();
            cpBook.setId(book.getId());
            cpBook.setName(book.getBookName());
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String key = owchPartner.getApiKey();
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, CHANG_JIANG_MAC, key, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("data", ChangJiangBook.class);
        xstream.aliasField("cname", ChangJiangBook.class, "cName");
        xstream.aliasField("bookname", ChangJiangBook.class, "bookName");
        xstream.aliasField("bookid", ChangJiangBook.class, "id");
        xstream.aliasField("bookpic", ChangJiangBook.class, "coven");
        xstream.aliasField("zzjs", ChangJiangBook.class, "brief");
        xstream.aliasField("authorname", ChangJiangBook.class, "author");
        xstream.aliasField("writestatus", ChangJiangBook.class, "status");
        xstream.ignoreUnknownElements();
        ChangJiangBook changJiangBook = (ChangJiangBook) xstream.fromXML(response);
        if (changJiangBook == null) {
            throw new Exception("书籍信息为空");
        }
        CPBook cpBook = new CPBook();
        cpBook.setId(changJiangBook.getId());
        cpBook.setName(changJiangBook.getBookName());
        cpBook.setAuthor(changJiangBook.getAuthor());
        cpBook.setBrief(changJiangBook.getBrief());
        cpBook.setCover(changJiangBook.getCoven());
        cpBook.setCategory(changJiangBook.getcName());// 所属分类
        cpBook.setCompleteStatus(changJiangBook.getStatus());

        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> columeList = new ArrayList<>();
        String key = owchPartner.getApiKey();
        String bookInfoUrl = owchPartner.getChapterListUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, CHANG_JIANG_MAC, key, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("data", ChangJiangVolumns.class);
        xstream.alias("vol", ChangJiangVolumn.class);
        xstream.alias("chapter", ChangjiangChapter.class);
        xstream.addImplicitCollection(ChangJiangVolumns.class, "changJiangVolumnList");
        xstream.addImplicitCollection(ChangJiangVolumn.class, "changjiangChapters");
        xstream.aliasField("volumename", ChangJiangVolumn.class, "volumnName");
        xstream.aliasField("chaptername", ChangjiangChapter.class, "chapterName");
        xstream.aliasField("chapterid", ChangjiangChapter.class, "id");
        ChangJiangVolumns changJiangVolumns = (ChangJiangVolumns) xstream.fromXML(response);
        if (changJiangVolumns == null) {
            throw new Exception("卷列表为空");
        }
        List<ChangJiangVolumn> changJiangVolumnList = changJiangVolumns.getChangJiangVolumnList();
        if (CollectionUtils.isEmpty(changJiangVolumnList)) {
            throw new Exception("卷列表为空");
        }

        for (int i = 0; i < changJiangVolumnList.size(); i++) {
            ChangJiangVolumn changJiangVolumn = changJiangVolumnList.get(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setName(changJiangVolumn.getVolumnName());
            cpVolume.setId(String.valueOf(i));
            List<CPChapter> chapterlist = new ArrayList<>();
            List<ChangjiangChapter> changjiangChapters = changJiangVolumn.getChangjiangChapters();
            if (changjiangChapters == null || changjiangChapters.size() < 1) {
                logger.info("卷id：{} 的章节列表为空", i);
                continue;
            }
            for (int j = 0; j < changjiangChapters.size(); j++) {
                ChangjiangChapter changjiangChapter = changjiangChapters.get(j);
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(String.valueOf(changjiangChapter.getId()));
                cpChapter.setName(changjiangChapter.getChapterName());
                chapterlist.add(cpChapter);
            }
            cpVolume.setChapterList(chapterlist);
            columeList.add(cpVolume);
        }


        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        String key = owchPartner.getApiKey();
        String chapterInfoUrl = owchPartner.getChapterInfoUrl();
        String bookInfoUrl = MessageFormat.format(chapterInfoUrl, CHANG_JIANG_MAC, key, cpBookId, chapterId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        String content = root.getText();
        content = content.replaceAll("<br>", "\n\r");
        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(content);
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.CHANG_JIANG};
    }

}
