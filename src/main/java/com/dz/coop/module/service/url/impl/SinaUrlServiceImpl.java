package com.dz.coop.module.service.url.impl;


import com.dz.coop.common.util.TimeUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz
 * @create 2017-06-25 下午8:34
 */
@Service
public class SinaUrlServiceImpl implements UrlService{

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), sinaSign(owchPartner.getApiKey()));
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), sinaSign(owchPartner.getApiKey()), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), sinaSign(owchPartner.getApiKey()), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), sinaSign(owchPartner.getApiKey()), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIN_LANG};
    }


    public String sinaSign(String key) {
        return DigestUtils.md5Hex(TimeUtil.getToday(TimeUtil.formatyyyyMMdd) + key).toLowerCase();
    }
}
