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
 * @description: 蜜阅测试
 * @author: songwj
 * @date: 2019-04-04 11:49
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class MiYueServiceImplTest {

    @Resource(name = "miYueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 88L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("691f71e2456dae0387a2e669c4d61a5d");
        owchPartner.setName("蜜阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://open.mixs.cn/index.php/api/book/bookList/AppID/KWILl7/ApiID/1557884097");
        partnerUrl.setBookInfoUrl("http://open.mixs.cn/index.php/api/book/bookDetail/AppID/KWILl7/ApiID/1557884109/BookId/{0}");
        partnerUrl.setChapterListUrl("http://open.mixs.cn/index.php/api/book/chapterList/AppID/KWILl7/ApiID/1557884123/BookId/{0}");
        partnerUrl.setChapterInfoUrl("http://open.mixs.cn/index.php/api/book/chapterContent/AppID/KWILl7/ApiID/1557884359/BookId/{0}/ChapterId/{1}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "56467");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "56467");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "56467", null, "4845743");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}