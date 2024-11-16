package com.dz.coop.module.service.cp.impl;

import com.dz.coop.TestBase;
import com.dz.coop.common.util.EscapeUtil;
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

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 米读文学接口测试
 * @author: songwj
 * @date: 2020-06-23 20:49
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class MiDuWenXueServiceImplTest extends TestBase {

    @Resource(name="miDuWenXueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 332L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("10008");
        owchPartner.setApiKey("dzkj_93ad391295e05d9147563783122528fd");
        owchPartner.setName("米读文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://openapi.miduwenxue.com/books?client_id={0}&request_time={1}&sign={2}");
        partnerUrl.setBookInfoUrl("https://openapi.miduwenxue.com/books/{0}?client_id={1}&request_time={2}&sign={3}");
        partnerUrl.setChapterListUrl("https://openapi.miduwenxue.com/books/{0}/chapters?client_id={1}&request_time={2}&sign={3}");
        partnerUrl.setChapterInfoUrl("https://openapi.miduwenxue.com/books/{0}/chapters/{1}?client_id={2}&request_time={3}&sign={4}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "106");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "100000034");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "906", null, "196130");
        System.out.println(EscapeUtil.escape(cpChapter.getContent()));
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}