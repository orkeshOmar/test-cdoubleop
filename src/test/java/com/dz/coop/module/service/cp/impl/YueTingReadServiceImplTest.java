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
 * @description: 阅庭书院
 * @author: songwj
 * @date: 2019-12-04 21:06
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class YueTingReadServiceImplTest {

    @Resource(name="yueTingReadServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 120L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("");
        owchPartner.setName("阅庭书院");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.ytread.com/api/dianzhong/getBookList.php");
        partnerUrl.setBookInfoUrl("http://www.ytread.com/api/dianzhong/getBookInfo.php?book_id={0}");
        partnerUrl.setChapterListUrl("http://www.ytread.com/api/dianzhong/getVolumeList.php?book_id={0}");
        partnerUrl.setChapterInfoUrl("http://www.ytread.com/api/dianzhong/getChapterInfo.php?book_id={0}&chapter_id={1}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "152");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "152");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "152", null, "5281");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}