package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 竹节接口测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ZhuJieServiceImplTest {

    @Resource(name = "zhuJieServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 20799L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("6");
        owchPartner.setName("竹节短篇");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://zhujie.fengzhir.top/api/v1/distribute/getBookList");
        partnerUrl.setBookInfoUrl("https://zhujie.fengzhir.top/api/v1/distribute/getBook");
        partnerUrl.setChapterListUrl("https://zhujie.fengzhir.top/api/v1/distribute/getBook");
        partnerUrl.setChapterInfoUrl("https://zhujie.fengzhir.top/api/v1/distribute/getChapter");
        partnerUrl.setCategoryListUrl("https://zhujie.fengzhir.top/api/v1/distribute/getTags");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(cpBookList.toString());

    }

    @Test
    public void getBookInfo() throws Exception {
        String book_id="18651";
        CPBook cpBook = clientService.getBookInfo(owchPartner,book_id);
        System.out.println(cpBook.toString());
    }

    @Test
    public void getVolumeList() throws Exception {
        String book_id="18651";
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner,book_id);
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        String book_id="18651";
        String cpChapterId="78434";

        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner,book_id, null, cpChapterId);
        System.out.println(cpChapter.toString());
    }

    @Test
    public void getCpCategoryList() throws Exception {
        List<CPCategory> cpCategoryList = clientService.getCpCategoryList(owchPartner);
        System.out.println(cpCategoryList.toString());
    }

}