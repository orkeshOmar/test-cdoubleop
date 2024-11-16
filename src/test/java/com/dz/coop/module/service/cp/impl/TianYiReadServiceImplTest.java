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
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 天翼阅读
 * @author: songwj
 * @date: 2019-05-05 17:25
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class TianYiReadServiceImplTest {

    @Resource(name = "tianYiReadServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 133L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("bjdztech");
        owchPartner.setApiKey("bjlP0w173wkd1");
        owchPartner.setName("天翼阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://open.alphafun.com/su-open/ycopen/querybooks.json?client_id={0}&sign={1}");
        partnerUrl.setBookInfoUrl("http://open.alphafun.com/su-open/ycopen/querybook.json?client_id={0}&sign={1}&book_id={2}");
        partnerUrl.setChapterListUrl("http://open.alphafun.com/su-open/ycopen/querychapters.json?client_id={0}&sign={1}&book_id={2}");
        partnerUrl.setChapterInfoUrl("http://open.alphafun.com/su-open/ycopen/querychapter.json?client_id={0}&sign={1}&book_id={2}&chapter_id={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "79020");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "79020");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "79020", null, "1");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}