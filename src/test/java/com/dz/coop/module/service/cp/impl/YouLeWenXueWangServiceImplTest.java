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
 * @description: 有乐文学网接口测试
 * @author: songwj
 * @date: 2019-08-21 17:39
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class YouLeWenXueWangServiceImplTest {

    @Resource(name = "youLeWenXueWangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 272L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("dianzhong");
        owchPartner.setApiKey("a1ef4be855cfb867f7f9bc8e05bf30e9");
        owchPartner.setName("有乐文学网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://openapi.iyoule.com/std/{0}/booklist");
        partnerUrl.setBookInfoUrl("http://openapi.iyoule.com/std/{0}/bookinfo?bookid={1}");
        partnerUrl.setChapterListUrl("http://openapi.iyoule.com/std/{0}/chapterlist?bookid={1}");
        partnerUrl.setChapterInfoUrl("http://openapi.iyoule.com/std/{0}/chapterinfo?bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "7630");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "7630");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "7630", null, "1393551");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}