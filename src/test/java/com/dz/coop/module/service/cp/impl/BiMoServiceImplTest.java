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
 * @description: 笔墨对接测试
 * @author: songwj
 * @date: 2021-03-26 16:39
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class BiMoServiceImplTest extends TestBase {

    @Resource(name="biMoServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 386L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("efTu3wThAdCyL6ZX");
        owchPartner.setApiKey("AjDNk43Av7QZrlp68k9esUmGdetLYvsJ");
        owchPartner.setName("笔墨");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://open.quick.bimo8.com/output/v1/novel?time={0}&apiKey={1}&signType=MD5&sign={2}");
        partnerUrl.setBookInfoUrl("https://open.quick.bimo8.com/output/v1/novel?time={0}&apiKey={1}&signType=MD5&sign={2}");
        partnerUrl.setChapterListUrl("https://open.quick.bimo8.com/output/v1/novel/{0}/chapter?time={1}&page={2}&pagesize={3}&apiKey={4}&signType=MD5&sign={5}");
        partnerUrl.setChapterInfoUrl("https://open.quick.bimo8.com/output/v1/novel/{0}/chapter/{1}/content?time={2}&apiKey={3}&signType=MD5&sign={4}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "xroZW0_23171451480");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "xroZW0_23171451480");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "xroZW0_23171451480", null, "7Zl9AM_7511192638");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}