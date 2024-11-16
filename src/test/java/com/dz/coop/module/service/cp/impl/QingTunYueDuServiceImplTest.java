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

/**
 * @project: coop-client
 * @description: 青豚阅读接口测试
 * @author: songwj
 * @date: 2019-01-09 11:36
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2018 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class QingTunYueDuServiceImplTest {

    @Resource(name = "qingTunYueDuServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 244L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("10009");
        owchPartner.setApiKey("ef0f477b0a734e069b4a2dc0254a0eae");
        owchPartner.setName("青豚阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.iqingtun.com/api/v1/getBookList?appId={0}&appsecret={1}");
        partnerUrl.setBookInfoUrl("http://api.iqingtun.com/api/v1/getBookInfo?appId={0}&appsecret={1}&bookId={2}");
        partnerUrl.setChapterListUrl("http://api.iqingtun.com/api/v1/getChapterList?appId={0}&appsecret={1}&bookId={2}");
        partnerUrl.setChapterInfoUrl("http://api.iqingtun.com/api/v1/getChapter?appId={0}&appsecret={1}&bookId={2}&chapterId={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "1");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1", null, "16329");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}