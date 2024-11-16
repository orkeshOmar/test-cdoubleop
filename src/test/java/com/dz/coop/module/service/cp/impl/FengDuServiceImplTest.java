package com.dz.coop.module.service.cp.impl;

import com.dz.coop.TestBase;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: coop-client
 * @description: 疯读对接测试
 * @author: songwj
 * @date: 2021-02-05 10:45
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class FengDuServiceImplTest extends TestBase {

    @Resource(name="fengDuServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 380L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("100220");
        owchPartner.setApiKey("UdIKKKKK7TOmU");
        owchPartner.setName("疯读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://open.shdengyong.com/api/v1/partner/booklist?partner_id={0}&ts={1}&sign={2}");
        partnerUrl.setBookInfoUrl("http://open.shdengyong.com/api/v1/partner/book?partner_id={0}&ts={1}&sign={2}&book_id={3}");
        partnerUrl.setChapterListUrl("http://open.shdengyong.com/api/v1/partner/chapters?partner_id={0}&sign={1}&ts={2}&book_id={3}");
        partnerUrl.setChapterInfoUrl("http://open.shdengyong.com/api/v1/partner/chapter/content?partner_id={0}&sign={1}&ts={2}&book_id={3}&chapter_id={4}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "16999");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "16999");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "16999", null, "1");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}