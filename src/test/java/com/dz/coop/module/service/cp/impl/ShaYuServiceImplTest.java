package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.glory.common.tools.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 傻鱼接口测试
 * @author: songwj
 * @date: 2019-03-27 17:44
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ShaYuServiceImplTest {

    @Resource(name = "shaYuServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 197L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("b82ab1fec00e27e437e9e5f704819823");
        owchPartner.setName("傻鱼中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.shayuu.com/channel/getbooklist");
        partnerUrl.setBookInfoUrl("http://api.shayuu.com/?m=home&c=common&a=book");
        partnerUrl.setChapterListUrl("http://api.shayuu.com/channel/getbookchapterlist");
        partnerUrl.setChapterInfoUrl("http://api.shayuu.com/channel/getbookchapter");
        partnerUrl.setCategoryListUrl("https://api.shayuu.com/?m=home&c=common&a=categories");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        clientService.getBookList(owchPartner).forEach(cpBook -> {
            try {
                CPBook book = clientService.getBookInfo(owchPartner, cpBook.getId());
                if (StringUtils.indexOfAny(book.getCategory(), "1", "2", "3") == -1) {
                    System.out.println("该书不存在合法的category");
                    System.out.println(JsonUtils.toJSON(cpBook));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
//        CPBook cpBook = clientService.getBookInfo(owchPartner, "1517");
//        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1517");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1517", null, "1813");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

    @Test
    public void getCpCategoryList() throws Exception {
        List<CPCategory> categoryList = clientService.getCpCategoryList(owchPartner);
        List<Map<String, Object>> cate = JsonUtil.readValue(JsonUtils.toJSON(categoryList), new TypeReference<List<Map<String, Object>>>() {
        });
        System.out.println(cate);
    }

}