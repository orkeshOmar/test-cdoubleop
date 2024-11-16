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
 * @description: 爱奇艺
 * @author: songwj
 * @date: 2019-04-08 15:59
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class AiQiYiServiceImplTest {

    @Resource(name="aiQiYiServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 235L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("kuaikaniQiYi");
        owchPartner.setApiKey("aniQiYiafy");
        owchPartner.setName("爱奇艺");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://legowenxueapi.iqiyi.com/partner/bookList?identity={0}");
        partnerUrl.setBookInfoUrl("http://legowenxueapi.iqiyi.com/partner/bookInfo?identity={0}&bookId={1}");
        partnerUrl.setChapterListUrl("http://legowenxueapi.iqiyi.com/partner/bookStructure?identity={0}&bookId={1}");
        partnerUrl.setChapterInfoUrl("http://legowenxueapi.iqiyi.com/partner/chapterInfo?identity={0}&chapterId={1}&sign={2}&verify={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "205991139");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "205991139");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "205991139", null, "1509139341");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}