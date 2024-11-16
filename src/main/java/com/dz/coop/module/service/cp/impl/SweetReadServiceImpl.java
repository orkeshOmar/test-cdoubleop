package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.model.vo.SweetReadBook;
import com.dz.coop.module.model.vo.SweetReadBooks;
import com.dz.coop.module.model.vo.SweetReadChapter;
import com.dz.coop.module.model.vo.SweetReadChapters;
import com.dz.coop.module.service.cp.ClientService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SweetReadServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String sign = owchPartner.getApiKey();
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, "booklist", sign);
        String response = HttpUtil.sendGet(bookListUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("books", SweetReadBooks.class);
        xstream.alias("book", SweetReadBook.class);
        xstream.aliasField("id", SweetReadBook.class, "id");
        xstream.aliasField("name", SweetReadBook.class, "name");
        xstream.aliasField("updatetime", SweetReadBook.class, "updateTime");
        xstream.aliasField("isfinish", SweetReadBook.class, "isFinish");
        xstream.addImplicitCollection(SweetReadBooks.class, "books");
        SweetReadBooks sweetReadBooks = (SweetReadBooks) xstream.fromXML(response);
        List<SweetReadBook> books = sweetReadBooks.getBooks();
        if (CollectionUtils.isEmpty(books)) {
            throw new Exception("书籍列表为空");
        }
        for (SweetReadBook book : books) {
            CPBook cpBook = new CPBook();
            cpBook.setId(book.getId());
            cpBook.setName(book.getName());
            cpBook.setCompleteStatus(String.valueOf(book.getIsFinish()));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String sign = owchPartner.getApiKey();
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, "book", sign, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("book", SweetReadBook.class);
        xstream.aliasField("id", SweetReadBook.class, "id");
        xstream.aliasField("category", SweetReadBook.class, "category");
        xstream.aliasField("name", SweetReadBook.class, "name");
        xstream.aliasField("author", SweetReadBook.class, "author");
        xstream.aliasField("keywords", SweetReadBook.class, "keywords");
        xstream.aliasField("words", SweetReadBook.class, "words");
        xstream.aliasField("cover", SweetReadBook.class, "cover");
        xstream.aliasField("summary", SweetReadBook.class, "summary");
        SweetReadBook sweetReadBook = (SweetReadBook) xstream.fromXML(response);
        CPBook cpBook = new CPBook();
        cpBook.setId(sweetReadBook.getId());
        cpBook.setName(sweetReadBook.getName());
        cpBook.setAuthor(sweetReadBook.getAuthor());
        cpBook.setBrief(sweetReadBook.getSummary());
        cpBook.setCover(sweetReadBook.getCover());
        cpBook.setCategory(sweetReadBook.getCategory());// 所属分类
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> columeList = new ArrayList<>();
        String sign = owchPartner.getApiKey();
        String bookInfoUrl = owchPartner.getChapterListUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, "chapters", sign, bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("chapters", SweetReadChapters.class);
        xstream.alias("chapter", SweetReadChapter.class);
        xstream.addImplicitCollection(SweetReadChapters.class, "chapters");
        xstream.aliasField("id", SweetReadChapter.class, "id");
        xstream.aliasField("title", SweetReadChapter.class, "title");
        xstream.aliasField("isVip", SweetReadChapter.class, "isVip");
        xstream.aliasField("volumeOrder", SweetReadChapter.class, "volumeOrder");
        xstream.aliasField("words", SweetReadChapter.class, "words");
        xstream.aliasField("chapterOrder", SweetReadChapter.class, "chapterOrder");
        xstream.aliasField("updatetime", SweetReadChapter.class, "updateTime");
        xstream.aliasField("url", SweetReadChapter.class, "url");
        SweetReadChapters chapters = (SweetReadChapters) xstream.fromXML(response);
        List<SweetReadChapter> chapterList = chapters.getChapters();
        CPVolume cpVolume = new CPVolume();
        cpVolume.setId("1");
        cpVolume.setName("");
        List<CPChapter> chapterlist = new ArrayList<CPChapter>();
        CPChapter cpChapter = null;
        for (SweetReadChapter chapter : chapterList) {
            cpChapter = new CPChapter();
            cpChapter.setId(String.valueOf(chapter.getId()));
            cpChapter.setName(chapter.getTitle());
            chapterlist.add(cpChapter);
        }
        cpVolume.setChapterList(chapterlist);
        columeList.add(cpVolume);

        return columeList;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        CPChapter cpChapter = null;
        String sign = owchPartner.getApiKey();
        String chapterInfoUrl = owchPartner.getChapterInfoUrl();
        String bookInfoUrl = MessageFormat.format(chapterInfoUrl, "chapter", sign, cpBookId, chapterId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("chapterContent", SweetReadChapter.class);
        xstream.aliasField("id", SweetReadChapter.class, "id");
        xstream.aliasField("content", SweetReadChapter.class, "content");
        SweetReadChapter chapter = (SweetReadChapter) xstream.fromXML(response);
        cpChapter = new CPChapter();
        cpChapter.setId(String.valueOf(chapter.getId()));
        String content = chapter.getContent();
        Pattern pattern = Pattern.compile("<br/>");
        Matcher matcher = pattern.matcher(content);
        content = matcher.replaceAll("\n\r");
        cpChapter.setContent(content);

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.TIAN_YUE_DU};
    }

}
