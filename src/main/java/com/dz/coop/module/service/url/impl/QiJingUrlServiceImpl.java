package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * @create 2017-06-27 上午11:26
 */
@Service
public class QiJingUrlServiceImpl implements UrlService{


    private final String appId = "dianzhong";
    private final String key = "ousdof9898023";

    @Override
    public String getBookListUrl(Partner owchPartner) {
        String sign = DigestUtils.md5Hex(appId + "&" + key);
        String bookListParam = String.format("appId=%s&sign=%s", appId, sign);
        return owchPartner.getBookListUrl() + "?" + bookListParam;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(appId + "&" + bookId + "&" + key);
        String param = String.format("appId=%s&bookId=%s&sign=%s", appId, bookId, sign);
        return owchPartner.getBookInfoUrl() + "?" + param;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(appId + "&" + bookId + "&" + key);
        String param = String.format("appId=%s&bookId=%s&sign=%s", appId, bookId, sign);
        return owchPartner.getChapterListUrl() + "?" + param;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String sign = DigestUtils.md5Hex(appId + "&" + cpBookId + "&" + chapterId + "&" + key);
        String param = String.format("appId=%s&bookId=%s&chapterId=%s&sign=%s", appId, cpBookId, chapterId, sign);
        return owchPartner.getChapterInfoUrl() + "?" + param;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QI_JING};
    }


}
