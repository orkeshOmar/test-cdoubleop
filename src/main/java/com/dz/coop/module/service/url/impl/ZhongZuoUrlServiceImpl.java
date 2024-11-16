package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * @create 2017-06-24 下午5:59
 */
@Service
public class ZhongZuoUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl() + 0;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + bookId;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + bookId;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + chapterId;
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIN_FENG};
    }

}
