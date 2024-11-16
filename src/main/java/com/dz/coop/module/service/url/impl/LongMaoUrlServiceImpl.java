package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * @create 2017-06-24 下午10:28
 * 龙猫阅读
 */
@Service
public class LongMaoUrlServiceImpl implements UrlService{


    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String bookInfoParam = String.format("book_id=%s", bookId);
        return handleUrl(owchPartner.getBookInfoUrl(), bookInfoParam);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String volumeStrParam = String.format("book_id=%s", bookId);
        return handleUrl(owchPartner.getChapterListUrl(), volumeStrParam);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String chapterInfoParam = String.format("book_id=%s&chapter_id=%s", cpBookId, chapterId);
        return handleUrl(owchPartner.getChapterInfoUrl(), chapterInfoParam);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.LONG_MAO_YUE_DU};
    }



    private String handleUrl(String url, String suffix) {
        if (url.indexOf("?") == -1) {
            url += "?";
        } else {
            url = url + "&";
        }
        return url + suffix;
    }
}
