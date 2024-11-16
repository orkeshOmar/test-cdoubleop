package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.mapper.PartnerMapper;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author panqz
 * @create 2017-06-23 下午1:38
 */
@Service
public class DianZhongServiceImpl implements ClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(DianZhongServiceImpl.class);

    @Resource
    private PartnerMapper partnerMapper;
    
    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        List<CPBook> cpBooks = new ArrayList<>();
        String bookListUrl = owchPartner.getBookListUrl();
        bookListUrl = MessageFormat.format(bookListUrl, owchPartner.getApiKey());
        String response = HttpUtil.sendGet(bookListUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator("item");
        while (iterator.hasNext()) {
            CPBook cpBook = new CPBook();
            Element item = iterator.next();
            cpBook.setId(item.elementText("id"));
            cpBook.setName(item.elementText("bookname"));
            cpBooks.add(cpBook);
        }
        return cpBooks;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String bookInfoUrl = owchPartner.getBookInfoUrl();
        bookInfoUrl = MessageFormat.format(bookInfoUrl, owchPartner.getApiKey(), bookId);
        String response = HttpUtil.sendGet(bookInfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element data = document.getRootElement();
        CPBook cpBook = new CPBook();
        
        cpBook.setId(data.elementText("bookid"));
        cpBook.setName(data.elementText("bookname"));
        cpBook.setAuthor(data.elementText("authorname"));
        cpBook.setBrief(data.elementText("zzjs"));
        cpBook.setCover(data.elementText("bookpic"));
        // 所属分类id
        cpBook.setCategory(data.elementText("cid"));
        cpBook.setCompleteStatus(data.elementText("writestatus"));
        
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> cpVolumes = new ArrayList<>();
        String chapterListUrl = owchPartner.getChapterListUrl();
        chapterListUrl = MessageFormat.format(chapterListUrl, owchPartner.getApiKey(), bookId);
        String response = HttpUtil.sendGet(chapterListUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator("vol");
        int order = 0;
        while (iterator.hasNext()) {
            order++;
            Element volume = iterator.next();
            CPVolume cpVolume = new CPVolume();
            cpVolume.setId(String.valueOf(order));
            cpVolume.setName(volume.elementText("volumename"));

            List<CPChapter> chapterlist = new ArrayList<>();
            Iterator<Element> chapters = volume.elementIterator("chapter");
            while (chapters.hasNext()) {
                Element chapter = chapters.next();
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(chapter.elementText("chapterid"));
                cpChapter.setName(chapter.elementText("chaptername"));
                cpChapter.setIsFree(Integer.parseInt(chapter.elementText("license")));
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
        chapterinfoUrl = MessageFormat.format(chapterinfoUrl, owchPartner.getApiKey(), cpBookId, chapterId);
        String response = HttpUtil.sendGet(chapterinfoUrl);
        Document document = DocumentHelper.parseText(response);
        Element root = document.getRootElement();
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
        return new ThirdPart[]{ThirdPart.SONG_SHU_YUE_DU, ThirdPart.ZUI_QING_CHUN, ThirdPart.HAI_YUE_WANG, ThirdPart.DIAN_ZHONG_CI_YUAN, ThirdPart.PIN_QU, ThirdPart.WANG_YAO_ZU,
                ThirdPart.SONG_SHU_JING_PIN, ThirdPart.DIAN_ZHONG_JING_PIN, ThirdPart.YOU_SHU_QIAN_JUAN, ThirdPart.SONG_SHU_YUAN_CHUANG, ThirdPart.SHU_XIANG_SI_HAI,
                ThirdPart.YAN_RU_YU_XIAO_SHUO, ThirdPart.YING_XUE_DU_SHU, ThirdPart.BI_MO_SHU_XIANG, ThirdPart.BI_XIA_SHENG_HUA, ThirdPart.HUA_YUAN_LU_DUAN_PIAN, ThirdPart.MU_DAN_YUAN_DUAN_PIAN};
    }

}
