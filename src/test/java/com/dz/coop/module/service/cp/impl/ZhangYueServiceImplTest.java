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
 * @description: 掌阅科技接口测试
 * @author: songwj
 * @date: 2019-12-06 16:54
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ZhangYueServiceImplTest {

    @Resource(name="zhangYueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 229L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("112");
        owchPartner.setApiKey("c6dde6cd5dec5ac74292c0366fde920c");
        owchPartner.setName("北京掌阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.res.ireader.com/api/v2/book/bookList?clientId={0}&sign={1}&resType=json");
        partnerUrl.setBookInfoUrl("http://api.res.ireader.com/api/v2/book/bookInfo?clientId={0}&sign={1}&resType=json&bookId={2}");
        partnerUrl.setChapterListUrl("http://api.res.ireader.com/api/v2/book/chapterList?clientId={0}&sign={1}&resType=json&bookId={2}");
        partnerUrl.setChapterInfoUrl("http://api.res.ireader.com/api/v2/book/chapterInfo?clientId={0}&sign={1}&resType=json&bookId={2}&chapterId={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "1028626");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1028626");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1028626", null, "1509139341");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}