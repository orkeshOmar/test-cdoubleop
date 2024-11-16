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
 * @description: 当当接口测试
 * @author: songwj
 * @date: 2019-10-09 16:19
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class DangDangServiceImplTest {

    @Resource(name="dangDangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 53L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("bjdzkj");
        owchPartner.setApiKey("7b421e1e42557dde3a6e5eeae2af4032");
        owchPartner.setName("当当");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://dushare.dangdang.com/distribute/api.go?action=getBookUpdate&distributeCode={0}&key={1}&queryCount=100");
        partnerUrl.setBookInfoUrl("http://dushare.dangdang.com/distribute/api.go?action=getDistributeBookDetail&distributeCode={0}&key={1}&bookId={2}");
        partnerUrl.setChapterListUrl("http://dushare.dangdang.com/distribute/api.go?action=getChapterListByBook&distributeCode={0}&key={1}&queryCount=100&bookId={2}");
        partnerUrl.setChapterInfoUrl("http://dushare.dangdang.com/distribute/api.go?action=getChapterDetail&distributeCode={0}&key={1}&bookId={2}&chapterId={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "5288920");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "5288920");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "5288920", null, "14939917");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }
}