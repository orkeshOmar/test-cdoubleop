package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 今日头条接口测试
 * @author: songwj
 * @date: 2020-05-28 11:56
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class JinRiTouTiaoServiceImplTest {

    @Resource(name="jinRiTouTiaoServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 100400L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("6831480039091994631");
        owchPartner.setApiKey("0c7be1be13fb368266352d680f36003a");
        owchPartner.setName("今日头条");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://novel.snssdk.com/api/novel/partner/book_list?partner_id={0}&ts={1}&sign={2}");
        partnerUrl.setBookInfoUrl("https://novel.snssdk.com/api/novel/partner/book_info?partner_id={0}&ts={1}&book_id={2}&sign={3}");
        partnerUrl.setChapterListUrl("https://novel.snssdk.com/api/novel/partner/chapter_list?partner_id={0}&ts={1}&book_id={2}&sign={3}");
        partnerUrl.setChapterInfoUrl("https://novel.snssdk.com/api/novel/partner/chapter_info?partner_id={0}&ts={1}&chapter_id={2}&sign={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "6909448739476212749");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "6909448739476212749");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "6770996355633515524", null, "7109685295905407518");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}