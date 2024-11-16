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
 * @description: 有狐文化
 * @author: songwj
 * @date: 2019-04-26 16:22
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class YouHuServiceImplTest {

    @Resource(name = "youHuServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 233L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("CH1054268677676933120");
        owchPartner.setApiKey("7e396e87c1e211f28a09ea7917bd6f99");
        owchPartner.setName("有狐文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.yhwxc.com/api/books/?channel={0}&key={1}");
        partnerUrl.setBookInfoUrl("http://api.yhwxc.com/api/info/?channel={0}&key={1}&bookId={2}");
        partnerUrl.setChapterListUrl("http://api.yhwxc.com/api/chapters/?channel={0}&key={1}&bookId={2}");
        partnerUrl.setChapterInfoUrl("http://api.yhwxc.com/api/content/?channel={0}&key={1}&bookId={2}&chapterId={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "B1054025006415163392");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "B1054025006415163392");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "B1054025006415163392", null, "CA1118827123809792000");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}