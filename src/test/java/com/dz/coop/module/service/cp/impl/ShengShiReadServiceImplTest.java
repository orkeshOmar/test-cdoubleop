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

import static org.junit.Assert.*;

public class ShengShiReadServiceImplTest extends TestBase {

    @Resource(name = "shengShiReadServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 106L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("2941189e00e11d935bbccef248c636e1");
        owchPartner.setName("盛世阅读网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.s4yd.com/kuaikan/booklist?apikey={0}");
        partnerUrl.setBookInfoUrl("http://api.s4yd.com/kuaikan/bookdetail?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://api.s4yd.com/kuaikan/chapterlist?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://api.s4yd.com/kuaikan/chapterdetail?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "3939");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "3939");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "3939", null, "709679");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}