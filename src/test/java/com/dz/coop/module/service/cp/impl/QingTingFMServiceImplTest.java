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
 * @description: 蜻蜓FM接口对接测试
 * @author: songwj
 * @date: 2019-11-14 17:12
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class QingTingFMServiceImplTest {

    @Resource(name = "qingTingFMServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 30000001L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("");
        owchPartner.setName("蜻蜓FM");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://open.staging.qingting.fm/media/v7/channelondemands?access_token={0}");
        partnerUrl.setBookInfoUrl("https://open.staging.qingting.fm/media/v7/channelondemands/{0}?access_token={1}");
        partnerUrl.setChapterListUrl("https://open.staging.qingting.fm/media/v7/channelondemands/{0}/programs?access_token={1}");
        partnerUrl.setChapterInfoUrl("https://open.staging.qingting.fm/media/v7/audiostream/channelondemands/{0}/programs/{1}?access_token={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "99432");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "99432");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "99432", null, "9733585");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}