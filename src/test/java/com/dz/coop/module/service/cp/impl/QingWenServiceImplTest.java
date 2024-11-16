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
 * @description: 轻文
 * @author: songwj
 * @date: 2019-04-08 19:31
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class QingWenServiceImplTest {

    @Resource(name="qingWenServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 118L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("15");
        owchPartner.setApiKey("c20ca0f97ae0c0323f32c95a61646b0c");
        owchPartner.setName("轻文");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://angewomon.iqing.in/book/?client_id={0}&request_time={1}&sign={2}&offset={3}&limit={4}");
        partnerUrl.setBookInfoUrl("http://angewomon.iqing.in/book/?client_id={0}&request_time={1}&sign={2}");
        partnerUrl.setChapterListUrl("http://angewomon.iqing.in/volume/?client_id={0}&request_time={1}&bid={2}&sign={3}&offset={4}&limit={5};http://angewomon.iqing.in/chapter/?client_id={0}&request_time={1}&bid={2}&vid={3}&sign={4}&offset={5}&limit={6}");
        partnerUrl.setChapterInfoUrl("http://angewomon.iqing.in/content/?client_id={0}&request_time={1}&bid={2}&vid={3}&cid={4}&sign={5}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "12212");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "12212");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "12212", null, "1813");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}