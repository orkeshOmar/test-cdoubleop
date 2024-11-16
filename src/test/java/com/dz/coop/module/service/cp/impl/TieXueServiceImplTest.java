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
 * @description: 铁血接口测试
 * @author: songwj
 * @date: 2020-12-16 17:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class TieXueServiceImplTest extends TestBase {

    @Resource(name = "tieXueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 151L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("041e60d011932082");
        owchPartner.setName("铁血");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://hezuo.book.tiexue.net/kuaikan/kkbook.aspx?identity={0}&method={1}&lastUpdateTime={2}");
        partnerUrl.setBookInfoUrl("http://hezuo.book.tiexue.net/kuaikan/kkbook.aspx?identity={0}&method={1}&bookid={2}");
        partnerUrl.setChapterListUrl("http://hezuo.book.tiexue.net/kuaikan/kkbook.aspx?identity={0}&method={1}&bookid={2}");
        partnerUrl.setChapterInfoUrl("http://hezuo.book.tiexue.net/kuaikan/kkbook.aspx?identity={0}&method={1}&bookid={2}&volumeId={3}&chapterId={4}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "32720");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "32720");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "32720", null, "CA1118827123809792000");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}