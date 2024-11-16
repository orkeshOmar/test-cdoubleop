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
 * @description: 青草文化接口对接测试
 * @author: songwj
 * @date: 2020-02-10 19:00
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class QingCaoWenHuaServiceImplTest {

    @Resource(name = "qingCaoWenHuaServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 294L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("VBU72YMWNXSW");
        owchPartner.setApiKey("PLT4XA79E6T4CJOJT2DIM1DP");
        owchPartner.setName("青草文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://source.qc667.com/api/apibookstwo/books?appname={0}&appkeys={1}");
        partnerUrl.setBookInfoUrl("http://source.qc667.com/api/apibookstwo/bookInfo?appname={0}&appkeys={1}&bookid={2}");
        partnerUrl.setChapterListUrl("http://source.qc667.com/api/apibookstwo/chapter?appname={0}&appkeys={1}&bookid={2}");
        partnerUrl.setChapterInfoUrl("http://source.qc667.com/api/apibookstwo/chapterInfo?appname={0}&appkeys={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "1718");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1718");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1718", null, "626460");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}