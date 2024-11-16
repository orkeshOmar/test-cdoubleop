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
 * @description: 福州掌中云测试
 * @author: songwj
 * @date: 2019-05-09 17:44
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class FuZhouZhangZhongYunServiceImplTest {

    @Resource(name="fuZhouZhangZhongYunServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 259L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("7");
        owchPartner.setApiKey("e52dc661fb684d1bbb65fd95e3246fe5");
        owchPartner.setName("福州掌中云");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://inovel.818tu.com/partners/api/novel_list?partner_id={0}&key={1}");
        partnerUrl.setBookInfoUrl("https://inovel.818tu.com/partners/api/novel_info?partner_id={0}&key={1}&nid={2}");
        partnerUrl.setChapterListUrl("https://inovel.818tu.com/partners/api/catalog?partner_id={0}&key={1}&nid={2}");
        partnerUrl.setChapterInfoUrl("https://inovel.818tu.com/partners/api/chapter_content?partner_id={0}&key={1}&nid={2}&aid={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "4667");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "4667");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "4667", null, "2169041");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}