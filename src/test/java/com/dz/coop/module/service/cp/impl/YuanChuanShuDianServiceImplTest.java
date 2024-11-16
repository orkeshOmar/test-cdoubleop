package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: coop-client
 * @description: 原创书殿接口测试
 * @author: songwj
 * @date: 2020-02-05 20:37
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class YuanChuanShuDianServiceImplTest {

    @Resource(name = "yuanChuanShuDianServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 292L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("Dianzhong");
        owchPartner.setApiKey("99e2c6bec85380d86a0e0d40c69fe6cf");
        owchPartner.setName("原创书殿");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://cp.yqsd.cn/CP/BookSource/?method=booklist&mcp={0}");
        partnerUrl.setBookInfoUrl("http://cp.yqsd.cn/CP/BookSource/?method=bookinfo&mcp={0}&bid={1}");
        partnerUrl.setChapterListUrl("http://cp.yqsd.cn/CP/BookSource/?method=chapterlist&mcp={0}&bid={1}");
        partnerUrl.setChapterInfoUrl("http://cp.yqsd.cn/CP/BookSource/?method=chapter&mcp={0}&bid={1}&cid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "9155");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "9155");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "9155", null, "2046655");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

    @Test
    public void printCategory() {
        String url = "http://cp.yqsd.cn/CP/BookSource/?method=category&mcp=Dianzhong";
        String resp = HttpUtil.sendGet(url);
        JSONArray jsonArray = JSON.parseObject(resp).getJSONArray("data");
        System.out.println();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject.get("ntitle") + "\t" + jsonObject.get("cname") + "-" + jsonObject.get("ttitle") + "-" + jsonObject.get("ntitle"));
        }
    }

}