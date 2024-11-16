package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * @project: coop_client
 * @description: 获取经纶文学网Url
 * @author: songwj
 * @date: 2018-12-06 17:15
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2018 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class JingLunUrlServiceImpl implements UrlService {
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
        return new ThirdPart[]{ThirdPart.JING_LUN};
    }
}
