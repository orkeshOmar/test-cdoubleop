package com.dz.coop.module.service.cp.impl;

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
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 点众自有
 * @author: songwj
 * @date: 2019-03-18 14:15
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class DianZhongServiceImplTest {

    @Resource(name = "dianZhongServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        /*Long clientId = 2L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("1059f4c0fc9a47149d6a2646eb85ed62");
        owchPartner.setName("松鼠阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 64L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("6ea8ffcad2514e5992ec460363502e26");
        owchPartner.setName("最青春小说网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 122L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("96283a9909fc68c96fe4775af19faea9");
        owchPartner.setName("点众次元");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 232L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("a17b4a1fb5eff3c71fab07ece816d7fc");
        owchPartner.setName("王瑶组");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 268L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("86c1ed05c18a726ed57d4b93f84fef42");
        owchPartner.setName("松鼠精品");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 282L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("5bfd3eee5dcf958f5e4c974faf1de363");
        owchPartner.setName("点众精品");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);*/

        Long clientId = 355L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("84d5d4d70dbfdccd1355dd44cfb6ace6");
        owchPartner.setName("笔墨书香");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://101.201.154.127/api/cpbook/booklist.html?apikey={0}");
        partnerUrl.setBookInfoUrl("http://101.201.154.127/api/cpbook/bookinfo.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://101.201.154.127/api/cpbook/chapterlist.html?apikey={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://101.201.154.127/api/cpbook/chapterContent.html?apikey={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "100001");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "100001");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "100001", null, "16329");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}