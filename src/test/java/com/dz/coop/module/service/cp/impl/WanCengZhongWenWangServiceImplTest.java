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

public class WanCengZhongWenWangServiceImplTest extends TestBase {

    @Resource(name="wanCengZhongWenWangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 456L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("8");
        owchPartner.setApiKey("54a2ec5f4421fa24bfa9bf6461e649a2");
        owchPartner.setName("万层中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.oneceng.com/apis/share/get_book_list.php?ssid={0}");
        partnerUrl.setBookInfoUrl("http://www.oneceng.com/apis/share/get_book_info.php?ssid={0}&aid={1}");
        partnerUrl.setChapterListUrl("http://www.oneceng.com/apis/share/get_chapter_list.php?ssid={0}&aid={1}");
        partnerUrl.setChapterInfoUrl("http://www.oneceng.com/apis/share/get_chapter_content.php?ssid={0}&aid={1}&cid={2}");
        owchPartner.setUrl(partnerUrl);
    }


    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "101");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "101");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "101", null, "44032");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}