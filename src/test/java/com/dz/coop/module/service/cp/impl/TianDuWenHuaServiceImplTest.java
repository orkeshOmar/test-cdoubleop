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
 * @description: 天读文化接口测绘
 * @author: songwj
 * @date: 2021-05-11 15:55
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class TianDuWenHuaServiceImplTest extends TestBase {

    @Resource(name = "tianDuWenHuaServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 398L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("QYXRNdpYg9HGXJIfnm7qCTXAs");
        owchPartner.setName("天读文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.qinglongshu.com/api/dianzhong/getbookList?key={0}");
        partnerUrl.setBookInfoUrl("http://api.qinglongshu.com/api/dianzhong/getbookInfo?key={0}&bookId={1}");
        partnerUrl.setChapterListUrl("http://api.qinglongshu.com/api/dianzhong/getchapterList?key={0}&bookId={1}");
        partnerUrl.setChapterInfoUrl("http://api.qinglongshu.com/api/dianzhong/getchapterInfo?key={0}&bookId={1}&chapterId={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "907");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "907");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "907", null, "291543");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}