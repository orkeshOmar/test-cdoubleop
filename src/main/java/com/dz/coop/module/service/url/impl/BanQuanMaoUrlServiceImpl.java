package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @project: coop-client
 * @description: 版权猫Url服务
 * @author: songwj
 * @date: 2019-08-19 15:58
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BanQuanMaoUrlServiceImpl implements UrlService {

    @Override
    public String getBookListUrl(Partner owchPartner) {
        String sign = DigestUtils.md5Hex(owchPartner.getId() + owchPartner.getApiKey());
        return MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), sign);
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(owchPartner.getId() + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), sign, bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(owchPartner.getId() + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), sign, bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String sign = DigestUtils.md5Hex(owchPartner.getId() + owchPartner.getApiKey() + cpBookId + chapterId);
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), sign, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.BAN_QUAN_MAO};
    }

}