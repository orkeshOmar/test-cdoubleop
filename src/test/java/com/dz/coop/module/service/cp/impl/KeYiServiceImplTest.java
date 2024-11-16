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
 * @description: 苛苡
 * @author: songwj
 * @date: 2020-10-26 10:57
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class KeYiServiceImplTest extends TestBase {

    @Resource(name="keYiServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 357L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("384387fdc13742ad8f93e2be07b05b7e");
        owchPartner.setApiKey("");
        owchPartner.setName("苛苡");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://cpapi.nuan5.cn/api/book/list?client_id={0}");
        partnerUrl.setBookInfoUrl("http://cpapi.nuan5.cn/api/book/info?client_id={0}&id={1}");
        partnerUrl.setChapterListUrl("http://cpapi.nuan5.cn/api/chapter/volumes?client_id={0}&id={1}");
        partnerUrl.setChapterInfoUrl("http://cpapi.nuan5.cn/api/chapter/info?client_id={0}&id={1}&cid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "2002");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "2002");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "2002", null, "3003");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}