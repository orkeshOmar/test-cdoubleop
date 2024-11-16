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
 * @description: 九创文学接口测试
 * @author: songwj
 * @date: 2020-05-20 20:18
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class JiuChuangWenXueServiceImplTest {

    @Resource(name="jiuChuangWenXueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 320L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("dianzhong");
        owchPartner.setApiKey("dianzhong1234567890");
        owchPartner.setName("九创文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://119.45.115.168:10001/vangett/get_novel_list?apiid={0}&sign={1}");
        partnerUrl.setBookInfoUrl("http://119.45.115.168:10001/vangett/get_novel_info?apiid={0}&novelid={1}&sign={2}");
        partnerUrl.setChapterListUrl("http://119.45.115.168:10001/vangett/get_chapter_list?apiid={0}&novelid={1}&sign={2}");
        partnerUrl.setChapterInfoUrl("http://119.45.115.168:10001/vangett/get_chapter_content?apiid={0}&novelid={1}&chapterid={2}&sign={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void getBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "75");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "75");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "75", null, "8305");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}