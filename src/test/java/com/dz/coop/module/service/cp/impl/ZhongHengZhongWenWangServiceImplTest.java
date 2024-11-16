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
 * @description: 纵横中文网接口测试
 * @author: songwj
 * @date: 2020-10-23 19:33
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class ZhongHengZhongWenWangServiceImplTest extends TestBase {

    @Resource(name="zhongHengZhongWenWangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 356L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("0nucsrzdvp");
        owchPartner.setApiKey("ms6u4sp6imkzfeaf");
        owchPartner.setName("纵横中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.zongheng.com/commonrest?method=api.books&t={0}&api_key={1}&sig={2}");
        partnerUrl.setBookInfoUrl("http://api.zongheng.com/commonrest?method=book&book_id={0}&t={1}&api_key={2}&sig={3}");
        partnerUrl.setChapterListUrl("http://api.zongheng.com/commonrest?method=chapter.dir&book_id={0}&t={1}&api_key={2}&sig={3}");
        partnerUrl.setChapterInfoUrl("http://api.zongheng.com/commonrest?method=chapter&book_id={0}&chapter_id={1}&t={2}&api_key={3}&sig={4}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "957547");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "957547");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "957547", null, "60100317");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}