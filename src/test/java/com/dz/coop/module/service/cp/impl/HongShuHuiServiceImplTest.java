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
 * @description: 红书汇接口测试
 * @author: songwj
 * @date: 2019-07-31 20:24
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class HongShuHuiServiceImplTest {

    @Resource(name="hongShuHuiServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 157L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("100000023");
        owchPartner.setApiKey("dsfgghjkk");
        owchPartner.setName("红书汇");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.foxread.cn/apis/api/BookList.php?sid={0}&sign={1}");
        partnerUrl.setBookInfoUrl("http://www.foxread.cn/apis/api/BookInfo.php?sid={0}&sign={1}&bookid={2}");
        partnerUrl.setChapterListUrl("http://www.foxread.cn/apis/api/BookChapters.php?sid={0}&sign={1}&bookid={2}");
        partnerUrl.setChapterInfoUrl("http://www.foxread.cn/apis/api/BookChapterInfo.php?sid={0}&sign={1}&bookid={2}&chapterid={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "559");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "559");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "559", null, "23053");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}