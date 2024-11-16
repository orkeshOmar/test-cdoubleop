package com.dz.coop.module.service.cp.impl;

import com.dz.coop.TestBase;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2020-07-23 11:23
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class HuanYueShiDaiServiceImplTest extends TestBase {

    @Resource(name = "huanYueShiDaiServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 104L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e99c6fe0616111e784cf305a3a0b8514");
        owchPartner.setName("欢悦时代");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.ihuanyue.com/hysd-interface/bookList?cpId={0}&apikey={1}");
        partnerUrl.setBookInfoUrl("http://www.ihuanyue.com/hysd-interface/bookInfo?cpId={0}&apikey={1}&bookId={2}");
        partnerUrl.setChapterListUrl("http://www.ihuanyue.com/hysd-interface/sectionList?cpId={0}&apikey={1}&bookId={2}");
        partnerUrl.setChapterInfoUrl("http://www.ihuanyue.com/hysd-interface/sectionContent?cpId={0}&apikey={1}&bookId={2}&chapterId={3}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "601505");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "601505");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "601505", null, "85d753f9c9d348f8a6defca3d9b9fa10");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}