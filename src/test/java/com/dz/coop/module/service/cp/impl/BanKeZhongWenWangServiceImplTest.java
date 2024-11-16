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

/**
 * @project: coop-client
 * @description: 半刻中文网接口测试
 * @author: songwj
 * @date: 2020-02-13 15:00
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class BanKeZhongWenWangServiceImplTest {

    @Resource(name = "banKeZhongWenWangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 295L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("29");
        owchPartner.setApiKey("2e2d7aee6a40f3aa8f12bc0eca3db177");
        owchPartner.setName("半刻中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.bankexs.com/api/share/get_book_list.php?ssid={0}");
        partnerUrl.setBookInfoUrl("http://www.bankexs.com/api/share/get_book_info.php?ssid={0}&aid={1}");
        partnerUrl.setChapterListUrl("http://www.bankexs.com/api/share/get_chapter_list.php?ssid={0}&aid={1}");
        partnerUrl.setChapterInfoUrl("http://www.bankexs.com/api/share/get_chapter_content.php?ssid={0}&aid={1}&cid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "480");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "480");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "480", null, "198510");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}