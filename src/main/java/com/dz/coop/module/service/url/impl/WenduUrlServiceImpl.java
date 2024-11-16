package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;


/**
 * 温度书城
 *
 * @author panqz
 * @create 2017-06-27 上午11:26
 */
@Deprecated
//@Service
public class WenduUrlServiceImpl implements UrlService{


    private final String keyPath = "?apikey=d78cebaeddba5aeea42c62d3fa87bb11";


    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl() + keyPath;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + keyPath + "&bookid=" + bookId;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + keyPath + "&bookid=" + bookId;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + keyPath + "&bookid=" + cpBookId + "&chapterid=" + chapterId;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.WEN_DU_SHU_CHENG};
    }

}
