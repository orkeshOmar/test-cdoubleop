package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @project: coop-client
 * @description: 海阅接口地址
 * @author: songwj
 * @date: 2019-05-20 20:31
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class HaiYueUrlServiceImpl implements UrlService {

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HAI_YUE};
    }

}
