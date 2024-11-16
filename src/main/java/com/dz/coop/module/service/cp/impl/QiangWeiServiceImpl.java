package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author panqz 2017-10-17 上午10:40
 */
@Service
public class QiangWeiServiceImpl implements ClientService {


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {

        Element root = doGet(owchPartner.getBookListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), "GetAllBooks");
        List<Element> items = root.elements("item");
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        List<CPBook> cpBooks = new ArrayList<>(items.size());
        for (Element element : items) {
            CPBook cpBook = new CPBook();
            cpBook.setId(element.elementText("id"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {

        Element root = doGet(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), "GetBookInfo", bookId);

        CPBook cpBook = new CPBook();
        cpBook.setId(root.elementText("bookid"));
        cpBook.setName(root.elementText("bookname"));
        cpBook.setAuthor(root.elementText("authorname"));
        cpBook.setBrief(root.elementText("zzjs"));
        cpBook.setCover(root.elementText("bookpic"));
        cpBook.setCompleteStatus(root.elementText("finish"));

        return cpBook;

    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        Element root = doGet(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), "GetChapterList", bookId);
        List<Element> items = root.elements("vol");
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        List<CPVolume> cpVolumes = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            Element element = items.get(i);
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(StringUtils.isNotBlank(element.elementText("volID")) ? element.elementText("volID") : String.valueOf(i));
            cpVolume.setName(element.elementText("volumename"));

            List<Element> chapterIds = element.elements("chapterid");
            List<Element> chapterNames = element.elements("chaptername");
            if (CollectionUtils.isEmpty(chapterIds) || CollectionUtils.isEmpty(chapterNames) || chapterIds.size() != chapterNames.size()) {
                continue;
            }
            List<CPChapter> cpChapters = new ArrayList<>(chapterIds.size());
            for (int j = 0; j < chapterIds.size(); j++) {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapterIds.get(j).getText());
                cpChapter.setName(chapterNames.get(j).getText());
                cpChapters.add(cpChapter);
            }
            cpVolume.setChapterList(cpChapters);
            cpVolumes.add(cpVolume);
        }
        return cpVolumes;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {

        Element root = doGet(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), owchPartner.getApiKey(), "GetChapterInfo", cpBookId, chapterId);

        CPChapter cpChapter = new CPChapter();
        cpChapter.setContent(root.elementText("content"));

        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHU_JIANG_WEN_XUE, ThirdPart.QIANG_WEI};
    }

    private Element doGet(String url, String pid, String apiKey, String action) throws Exception {
        return doGet(url, pid, apiKey, action, null);
    }

    private Element doGet(String url, String pid, String apiKey, String action, String bookId) throws Exception {
        return doGet(url, pid, apiKey, action, bookId, null);
    }

    private Element doGet(String url, String pid, String apiKey, String action, String bookId, String chapterId) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        if (StringUtils.isNotBlank(bookId) && StringUtils.isNotBlank(chapterId)) {
            return DocumentHelper.parseText(HttpUtil.sendGet(MessageFormat.format(url, action, pid, timestamp, DigestUtils.md5Hex(DigestUtils.md5Hex(pid + apiKey + timestamp).toLowerCase()).toLowerCase(), bookId, chapterId))).getRootElement();
        }
        if (StringUtils.isNotBlank(bookId)) {
            return DocumentHelper.parseText(HttpUtil.sendGet(MessageFormat.format(url, action, pid, timestamp, DigestUtils.md5Hex(DigestUtils.md5Hex(pid + apiKey + timestamp).toLowerCase()).toLowerCase(), bookId))).getRootElement();
        }
        return DocumentHelper.parseText(HttpUtil.sendGet(MessageFormat.format(url, action, pid, timestamp, DigestUtils.md5Hex(DigestUtils.md5Hex(pid + apiKey + timestamp).toLowerCase()).toLowerCase()))).getRootElement();
    }


}
