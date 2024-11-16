package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @project: coop-client
 * @description: 获取娱阅看书Url
 * @author: songwj
 * @date: 2018-12-18 18:04
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2018 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class YuYueKanShuUrlServiceImpl implements UrlService {

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl().trim();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl().trim(), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl().trim(), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl().trim(), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YU_YUE_KAN_SHU};
    }

}
