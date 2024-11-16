package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.TestBase;
import com.dz.coop.common.util.HttpUtil;
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

/**
 * @project: coop-client
 * @description: 笔尚接口测试
 * @author: songwj
 * @date: 2020-09-11 11:13
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class BiShangServiceImplTest extends TestBase {

    @Resource(name="biShangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 350L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("2f5b683ab4ef27f8de4e43df19043aca");
        owchPartner.setName("笔尚");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://www.bsxiaoshuo.com/api/exp/out.php?gs=1&apikey={0}&method=booklist");
        partnerUrl.setBookInfoUrl("https://www.bsxiaoshuo.com/api/exp/out.php?gs=1&apikey={0}&method=info&bookid={1}");
        partnerUrl.setChapterListUrl("https://www.bsxiaoshuo.com/api/exp/out.php?gs=1&apikey={0}&method=chapters&bookid={1}");
        partnerUrl.setChapterInfoUrl("https://www.bsxiaoshuo.com/api/exp/out.php?gs=1&apikey={0}&method=chapter&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "1456");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "1456");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "1456", null, "323447");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

    @Test
    public void printCategory() {
        String url = "https://www.bsxiaoshuo.com/api/exp/out.php?gs=1&apikey=2f5b683ab4ef27f8de4e43df19043aca&method=class";
        String resp = HttpUtil.sendGet(url);
        JSONObject object = JSON.parseObject(resp);
        JSONArray jsonArray = object.getJSONArray("result");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject.get("category_id") + "\t" + jsonObject.get("category_name"));
        }
    }

}