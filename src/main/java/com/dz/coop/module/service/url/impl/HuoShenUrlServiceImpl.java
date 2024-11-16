package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * @create 2017-06-24 下午10:28
 * 火神
 */
@Service
public class HuoShenUrlServiceImpl implements UrlService{


    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + "/BookId=" + bookId;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + "/BookId=" + bookId;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + "/BookId=" + cpBookId + "/ChapterId=" + chapterId;
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HUO_SHEN};
    }
}
