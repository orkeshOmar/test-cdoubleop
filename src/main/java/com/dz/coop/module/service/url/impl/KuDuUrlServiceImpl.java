package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * 酷读
 *
 * @author panqz 2018-05-17 下午1:39
 */
@Service
public class KuDuUrlServiceImpl implements UrlService {
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl().trim();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return (owchPartner.getBookInfoUrl() + "/book_id/" + bookId).trim();
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return (owchPartner.getChapterListUrl() + "/book_id/" + bookId).trim();
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return (owchPartner.getChapterInfoUrl() + "/book_id/" + cpBookId + "/chapter_id/" + chapterId).trim();
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.KU_DU};
    }
}
