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
 * @description: 掌中文学接口测试
 * @author: songwj
 * @date: 2019-12-05 13:48
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("hot")
public class ZhangZhongServiceImplTest {

    @Resource(name = "zhangZhongServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 57L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("940aad0b-6d1a-4942-9489-3071d73f80b9");
        owchPartner.setName("掌中文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://m.51changdu.cn/output/getresource.svd?domain=getupdatelist&appid={0}&begintime={1}&endtime={2}&output=json");
        partnerUrl.setBookInfoUrl("https://m.51changdu.cn/output/getresource.svd?domain=getbookinfo&appid={0}&bookid={1}&output=json");
        partnerUrl.setChapterListUrl("https://m.51changdu.cn/output/getresource.svd?domain=getchapterlist&appid={0}&pageindex={1}&pagenumber={2}&bookid={3}&output=json");
        partnerUrl.setChapterInfoUrl("https://m.51changdu.cn/output/download.ashx?domain=downloadchapter&appid={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "1545048");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1545048");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1545048", null, "291189");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}