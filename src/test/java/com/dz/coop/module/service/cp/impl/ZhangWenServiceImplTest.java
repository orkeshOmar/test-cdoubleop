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
 * @description: 掌文对接测试
 * @author: songwj
 * @date: 2019-03-25 19:34
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ZhangWenServiceImplTest {

    @Resource(name = "zhangWenServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 256L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("306");
        owchPartner.setApiKey("jDSE2QBuUZfTEI0N");
        owchPartner.setName("掌文");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://partner.heiyan.com/data/api/booklist?pid={0}");
        partnerUrl.setBookInfoUrl("http://partner.heiyan.com/data/api/book?pid={0}&bookId={1}");
        partnerUrl.setChapterListUrl("http://partner.heiyan.com/data/api/chapterlist?pid={0}&bookId={1}");
        partnerUrl.setChapterInfoUrl("http://partner.heiyan.com/data/api/chapter?pid={0}&chapterId={1}&verify={2}&sign={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "52827");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "52827");
        List<CPVolume> cpVolumes1 = clientService.getVolumeList(owchPartner, "106075");
        List<CPVolume> cpVolumes2 = clientService.getVolumeList(owchPartner, "118084");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "52827", null, "662970");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}