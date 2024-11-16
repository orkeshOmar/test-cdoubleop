package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz
 * @create 2017-08-16 下午5:59
 */
@Service
public class LaoHuUrlServiceImpl implements UrlService {

    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        return MessageFormat.format(owchPartner.getBookListUrl(), clientId, sign);
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getBookInfoUrl(), clientId, sign, bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getChapterListUrl(), clientId, sign, bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + cpBookId + chapterId);
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), clientId, sign, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.LAO_HU_FA_WEI_XIAO_SHUO_WANG};
    }

}
