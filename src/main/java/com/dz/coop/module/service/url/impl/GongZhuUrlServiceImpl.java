package com.dz.coop.module.service.url.impl;

import com.dz.coop.common.util.MD5Util;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * 公主书城
 * @author luozukai
 */
@Service
public class GongZhuUrlServiceImpl implements UrlService {



    public static final String sid = "33";
    public static final String key = "gsk3e7xchg2hjssm";

    @Override
    public String getBookListUrl(Partner owchPartner) {
//        String clientId = commonService.getClientId(owchPartner);
//        String path = "sid="+clientId+"&page="+page;
//        String sign = MD5Util.MD5(path + "&key="+owc hPartner.getApiKey());
//        path += "&sign="+sign;
//        return owchPartner.getBooklistUrl() + "?" + path;
        return null;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String path = "aid="+bookId+"&sid="+sid;
        String sign = MD5Util.MD5(path + "&key="+key);
        path += "&sign="+sign;
        return owchPartner.getBookInfoUrl() + "?" + path;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String path = "aid="+bookId+"&sid="+sid;
        String sign = MD5Util.MD5(path + "&key="+key);
        path += "&sign="+sign;
        return owchPartner.getChapterListUrl() + "?" + path;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String path = "aid="+cpBookId +"&cid="+chapterId +"&sid="+sid;
        String sign = MD5Util.MD5(path + "&key="+key);
        path += "&sign="+sign;
        return owchPartner.getChapterInfoUrl() + "?" + path;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.GONG_ZHU_SHU_CHENG};
    }
}
