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

public class HaiNanZhangYueServiceImplTest extends TestBase {

    @Resource(name = "haiNanZhangYueServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 20107L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("189");
        owchPartner.setApiKey("ef2c2f3fb1983cd5b7ee8960bdfe6087");
        owchPartner.setName("海南掌阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.res.ireader.com/resplatform/api/v2/book/pageBookList?clientId={0}&sign={1}&resType=json");
        partnerUrl.setBookInfoUrl("http://api.res.ireader.com/resplatform/api/v2/book/bookInfo?clientId={0}&sign={1}&bookId={2}&resType=json");
        partnerUrl.setChapterListUrl("http://api.res.ireader.com/resplatform/api/v2/book/pageChapterList?clientId={0}&sign={1}&bookId={2}&resType=json");
        partnerUrl.setChapterInfoUrl("http://api.res.ireader.com/resplatform/api/v2/book/chapterInfo?clientId={0}&sign={1}&bookId={2}&chapterId={3}&resType=json");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "11033823");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "11033823");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "11033823", null, "240756");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}