package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.module.mapper.BookTypeTwoMapper;
import com.dz.coop.module.mapper.PartnerMapper;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.support.BookSupport;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * @project: coop-client
 * @description: 我方标准cp对接测试
 * @author: songwj
 * @date: 2018-12-18 18:18
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2018 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("hot")
public class CommonCpServiceImplTest {

    @Resource(name = "commonCpServiceImpl")
    private ClientService clientService;

    @Resource
    private PartnerMapper partnerMapper;

    @Resource
    private BookTypeTwoMapper bookTypeTwoMapper;

    private static Partner owchPartner = new Partner();

    static {
        /*Long clientId = 5L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e4da3b7fbbce2345d7772b0674a318d5");
        owchPartner.setName("创别书城（文博）");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://partner.chuangbie.com/ApiDianZhong/getBookList");
        partnerUrl.setBookInfoUrl("https://partner.chuangbie.com/ApiDianZhong/getBookInfo");
        partnerUrl.setChapterListUrl("https://partner.chuangbie.com/ApiDianZhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("https://partner.chuangbie.com/ApiDianZhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 12L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("c20ad4d76fe97759aa27a0c99bff6710");
        owchPartner.setName("点金堂");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.1001p.com/dianzhong/book_list.json?source=20056");
        partnerUrl.setBookInfoUrl("http://api.1001p.com/dianzhong/book_info.json?source=20056");
        partnerUrl.setChapterListUrl("http://api.1001p.com/dianzhong/chapters.json?source=20056");
        partnerUrl.setChapterInfoUrl("http://api.1001p.com/dianzhong/content.json?source=20056");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 13L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("7e51746feafa7f2621f71943da8f603c");
        owchPartner.setName("逸云书院");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://thirdapi.motie.com/api/yynovel/kuaikan/booklist");
        partnerUrl.setBookInfoUrl("http://thirdapi.motie.com/api/yynovel/kuaikan/books");
        partnerUrl.setChapterListUrl("http://thirdapi.motie.com/api/yynovel/kuaikan/chapterlist");
        partnerUrl.setChapterInfoUrl("http://thirdapi.motie.com/api/yynovel/kuaikan/content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 18L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("75");
        owchPartner.setApiKey("feeb8cf461e2aeaedbd5c45cff0a361a");
        owchPartner.setName("磨铁数盟原创");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://thirdapi.motie.com/api/motie/kuaikan/booklist");
        partnerUrl.setBookInfoUrl("http://thirdapi.motie.com/api/motie/kuaikan/books");
        partnerUrl.setChapterListUrl("http://thirdapi.motie.com/api/motie/kuaikan/chapterlist");
        partnerUrl.setChapterInfoUrl("http://thirdapi.motie.com/api/motie/kuaikan/content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 40L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("86b9b2f8b5ca8fbcc8ec6579adff87eb");
        owchPartner.setName("红薯网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.hongshu.com/empower4/dianzhong/getbooklists.php");
        partnerUrl.setBookInfoUrl("http://open.hotread.com/share-api/dianzhong/api/getBookInfo");
        partnerUrl.setChapterListUrl("http://open.hotread.com/share-api/dianzhong/api/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://open.hotread.com/share-api/dianzhong/api/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 42L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("c210316aa28429797908af0d5b50cad7");
        owchPartner.setName("书海小说网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://dianzhong.com.api.shuhai.com/getBookList/");
        partnerUrl.setBookInfoUrl("http://dianzhong.com.api.shuhai.com/getBookInfo/");
        partnerUrl.setChapterListUrl("http://dianzhong.com.api.shuhai.com/getVolumeList/");
        partnerUrl.setChapterInfoUrl("http://dianzhong.com.api.shuhai.com/getChapterInfo/");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 49L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("ded2d3d30720c9e297f9ec297962375e");
        owchPartner.setName("阅书小说网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://91baby.mama.cn/cp/third/dianzhong/getBookList");
        partnerUrl.setBookInfoUrl("http://91baby.mama.cn/cp/third/dianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://91baby.mama.cn/cp/third/dianzhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://91baby.mama.cn/cp/third/dianzhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 55L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("7306775ed317d2079c232e9eba97d55b");
        owchPartner.setName("网易");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://distapi.yuedu.163.com/venus/json/dianzhong/bookList.json?consumerKey=00264194");
        partnerUrl.setBookInfoUrl("http://distapi.yuedu.163.com/venus/json/dianzhong/bookInfo.json?consumerKey=00264194");
        partnerUrl.setChapterListUrl("http://distapi.yuedu.163.com/venus/json/dianzhong/sectionList.json?consumerKey=00264194");
        partnerUrl.setChapterInfoUrl("http://distapi.yuedu.163.com/venus/json/dianzhong/sectionContent.json?consumerKey=00264194");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 71L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("d84638859a470bd56bf124da80b3ca76");
        owchPartner.setName("博阅中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.lboyue.com/channel/custom/dianzhong/getbooklist");
        partnerUrl.setBookInfoUrl("http://api.lboyue.com/channel/custom/dianzhong/getbookinfo");
        partnerUrl.setChapterListUrl("http://api.lboyue.com/channel/custom/dianzhong/getbookchapterlist");
        partnerUrl.setChapterInfoUrl("http://api.lboyue.com/channel/custom/dianzhong/getbookchapter");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 117L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("");
        owchPartner.setName("瑶池文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.datangzww.com/api/empower_kuaikan/siteGetBkList.php?key=WT2738kd");
        partnerUrl.setBookInfoUrl("http://www.datangzww.com/api/empower_kuaikan/siteGetBkInfo.php?key=WT2738kd&bkId={0}");
        partnerUrl.setChapterListUrl("http://www.datangzww.com/api/empower_kuaikan/siteGetChList.php?key=WT2738kd&bkId={0}");
        partnerUrl.setChapterInfoUrl("http://www.datangzww.com/api/empower_kuaikan/siteGetChCon.php?key=WT2738kd&bkId={0}&chId={1}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 134L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("5f52031fab8fe579aedfbb468993d960");
        owchPartner.setName("醉唐中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://thirdapi.motie.com/api/zuitang/kuaikan/booklist");
        partnerUrl.setBookInfoUrl("http://thirdapi.motie.com/api/zuitang/kuaikan/books");
        partnerUrl.setChapterListUrl("http://thirdapi.motie.com/api/zuitang/kuaikan/chapterlist");
        partnerUrl.setChapterInfoUrl("http://thirdapi.motie.com/api/zuitang/kuaikan/content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 142L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("509d0e632c6674954d38483838f3632a");
        owchPartner.setName("阅心文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.shuyingread.com/dist/dianzhong/books.php");
        partnerUrl.setBookInfoUrl("http://www.shuyingread.com/dist/dianzhong/book.php");
        partnerUrl.setChapterListUrl("http://www.shuyingread.com/dist/dianzhong/chapters.php");
        partnerUrl.setChapterInfoUrl("http://www.shuyingread.com/dist/dianzhong/chapter.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 153L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("4fc8f8e36367a2f5ff42b38a489d4b3d");
        owchPartner.setName("红书馆");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://47.99.154.193/api/book/list");
        partnerUrl.setBookInfoUrl("http://47.99.154.193/api/book/info");
        partnerUrl.setChapterListUrl("http://47.99.154.193/api/chapter/list");
        partnerUrl.setChapterInfoUrl("http://47.99.154.193/api/chapter/info");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 157L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e00237b51133edeac62d0ac8129808d9");
        owchPartner.setName("红书汇");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://bookapi.ledu18.cn/api/dianzhong/book-list");
        partnerUrl.setBookInfoUrl("http://bookapi.ledu18.cn/api/dianzhong/book-info");
        partnerUrl.setChapterListUrl("http://bookapi.ledu18.cn/api/dianzhong/chapter-list");
        partnerUrl.setChapterInfoUrl("http://bookapi.ledu18.cn/api/dianzhong/content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 181L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("678a1020d3052bfa6d59281a67dd47de");
        owchPartner.setName("创策");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://miluzw.com/api/novelList_dz");
        partnerUrl.setBookInfoUrl("http://miluzw.com/api/novelIndfo_dz");
        partnerUrl.setChapterListUrl("http://miluzw.com/api/chapterList_dz");
        partnerUrl.setChapterInfoUrl("http://miluzw.com/api/chapterInfo_dz");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 171L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("7acad0dba92c9695b9a233d9d18088c");
        owchPartner.setName("尚阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.shangread.com/api.php/Dianzhong/booklist");
        partnerUrl.setBookInfoUrl("http://www.shangread.com/api.php/Dianzhong/bookinfo/book_id/{0}");
        partnerUrl.setChapterListUrl("http://www.shangread.com/api.php/Dianzhong/chapterlist/book_id/{0}");
        partnerUrl.setChapterInfoUrl("http://www.shangread.com/api.php/Dianzhong/chapterinfo/book_id/{0}/chapter_id/{1}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 187L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("11ee0d9c4905eda319490be34c02c153");
        owchPartner.setName("凤鸣轩");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www1.fmx.cn/api/dianzhong/get_book_list.php");
        partnerUrl.setBookInfoUrl("http://www1.fmx.cn/api/dianzhong/get_book_info.php");
        partnerUrl.setChapterListUrl("http://www1.fmx.cn/api/dianzhong/get_chapter_list.php");
        partnerUrl.setChapterInfoUrl("http://www1.fmx.cn/api/dianzhong/get_chapter_content.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 189L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("edba65d38188b0392fb9a5295f89958e");
        owchPartner.setName("白黑白");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://dianzhong.com.api.baiheibai.com/");
        partnerUrl.setBookInfoUrl("http://dianzhong.com.api.baiheibai.com/info/");
        partnerUrl.setChapterListUrl("http://dianzhong.com.api.baiheibai.com/chapters/");
        partnerUrl.setChapterInfoUrl("http://dianzhong.com.api.baiheibai.com/chapter/");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 195L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("bd8ea23236d1f2112f12359ca746adc3");
        owchPartner.setName("火星小说");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://open.hotread.com/share-api/dianzhong/api/getBookList");
        partnerUrl.setBookInfoUrl("http://open.hotread.com/share-api/dianzhong/api/getBookInfo");
        partnerUrl.setChapterListUrl("http://open.hotread.com/share-api/dianzhong/api/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://open.hotread.com/share-api/dianzhong/api/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 205L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("d1c480f2398e0b606b882bfc361566fb");
        owchPartner.setName("酷匠");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://reseller.kujiang.com/reseller/dz/get_book_list");
        partnerUrl.setBookInfoUrl("https://reseller.kujiang.com/reseller/dz/get_book_info");
        partnerUrl.setChapterListUrl("https://reseller.kujiang.com/reseller/dz/get_chapter_list");
        partnerUrl.setChapterInfoUrl("https://reseller.kujiang.com/reseller/dz/get_chapter_content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 206L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("c6b1038fc858d0fe4ce1e62ab973f38b");
        owchPartner.setName("春田小说");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.ctbook.cn/output.php/kuaikan/data?action=booklist&token=00f5a3fd56918c284bfec62df5d02f2c&who=kuaikan");
        partnerUrl.setBookInfoUrl("http://www.ctbook.cn/output.php/kuaikan/data?action=BookInfo&token=00f5a3fd56918c284bfec62df5d02f2c&who=kuaikan");
        partnerUrl.setChapterListUrl("http://www.ctbook.cn/output.php/kuaikan/data?action=ChapterList&token=00f5a3fd56918c284bfec62df5d02f2c&who=kuaikan");
        partnerUrl.setChapterInfoUrl("http://www.ctbook.cn/output.php/kuaikan/data?action=Chapter&token=00f5a3fd56918c284bfec62df5d02f2c&who=kuaikan");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 210L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e48279616c290d51ab5f3277be69895d");
        owchPartner.setName("喜马拉雅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://open.qijizuopin.com/dianzhong/getBookList");
        partnerUrl.setBookInfoUrl("https://open.qijizuopin.com/dianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("https://open.qijizuopin.com/dianzhong/getChapterList");
        partnerUrl.setChapterInfoUrl("https://open.qijizuopin.com/dianzhong/getChapterContent");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 218L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("cb7457fe7e22c92212048b7d4d21a463");
        owchPartner.setName("博闻");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://cp.lveshu.com/index.php?m=api/in&AppID=B34c9t&ApiID=1539147684");
        partnerUrl.setBookInfoUrl("http://cp.lveshu.com/index.php?m=api/in&AppID=B34c9t&ApiID=1539149483");
        partnerUrl.setChapterListUrl("http://cp.lveshu.com/index.php?m=api/in&AppID=B34c9t&ApiID=1539152693");
        partnerUrl.setChapterInfoUrl("http://cp.lveshu.com/index.php?m=api/in&AppID=B34c9t&ApiID=1539153035");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 221L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("41beca508c2b049db358ea890f0e8e73");
        owchPartner.setName("羽书");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://store.zhewenit.com/api/Exportdianzhong/getBookList");
        partnerUrl.setBookInfoUrl("http://store.zhewenit.com/api/Exportdianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://store.zhewenit.com/api/Exportdianzhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://store.zhewenit.com/api/Exportdianzhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 226L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("98f25cc3a61ead960bea7a3ceff42046");
        owchPartner.setName("连众文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.Lzbook.com/api/dots/books");
        partnerUrl.setBookInfoUrl("http://www.Lzbook.com/api/dots/book-info");
        partnerUrl.setChapterListUrl("http://www.Lzbook.com/api/dots/chapters");
        partnerUrl.setChapterInfoUrl("http://www.Lzbook.com/api/dots/chapter-info");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "240";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("1d456c5332b4ce29b1861375470bbf27");
        owchPartner.setName("娱阅看书");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://www.yuyueks.com/apis/dianzong/BookList.php");
        partnerUrl.setBookInfoUrl("http://www.yuyueks.com/apis/dianzong/BookInfo.php?book_id={0}");
        partnerUrl.setChapterListUrl("http://www.yuyueks.com/apis/dianzong/BookChapters.php?book_id={0}");
        partnerUrl.setChapterInfoUrl("http://www.yuyueks.com/apis/dianzong/BookChapterInfo.php?book_id={0}&chapter_id={1}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 242L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("98f25cc3a61ead960bea7a3ceff42046");
        owchPartner.setName("青椒阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.weieyd.com/apis/dianzhong/novels.php");
        partnerUrl.setBookInfoUrl("http://www.weieyd.com/apis/dianzhong/information.php");
        partnerUrl.setChapterListUrl("http://www.weieyd.com/apis/dianzhong/catalog.php");
        partnerUrl.setChapterInfoUrl("http://www.weieyd.com/apis/dianzhong/chapter.php");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "243";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("a345ba5a87227571c02d216368d858e6");
        owchPartner.setName("柚米文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://yoomiread.com/api/novelList_dz");
        partnerUrl.setBookInfoUrl("http://yoomiread.com/api/novelInfo_dz");
        partnerUrl.setChapterListUrl("http://yoomiread.com/api/chapterList_dz");
        partnerUrl.setChapterInfoUrl("http://yoomiread.com/api/chapterInfo_dz");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "245";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("3c03d2b74aa8385cbac2adab19fb5e1e");
        owchPartner.setName("白丁小说");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://211.149.160.62:8093/api/getBookList");
        partnerUrl.setBookInfoUrl("http://211.149.160.62:8093/api/getBookInfo");
        partnerUrl.setChapterListUrl("http://211.149.160.62:8093/api/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://211.149.160.62:8093/api/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "246";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("523f9eeb9bc24ea342ea8e11059c6704");
        owchPartner.setName("好阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://api.haoyued.com/dianzhong/booklist");
        partnerUrl.setBookInfoUrl("http://api.haoyued.com/dianzhong/bookinfo");
        partnerUrl.setChapterListUrl("http://api.haoyued.com/dianzhong/chapterlist");
        partnerUrl.setChapterInfoUrl("http://api.haoyued.com/dianzhong/chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "247";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("72ce383cd9ffac491d7aee7865332c63");
        owchPartner.setName("欣阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://panshibook.com/partners/DianzhongApi/novel_list");
        partnerUrl.setBookInfoUrl("http://panshibook.com/partners/DianzhongApi/novel_info");
        partnerUrl.setChapterListUrl("http://panshibook.com/partners/DianzhongApi/catalog");
        partnerUrl.setChapterInfoUrl("http://panshibook.com/partners/DianzhongApi/chapter_content");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "248";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("d20137ddef22f59c691159dbedb464ea");
        owchPartner.setName("轻漫文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://www.pinyuew.com/api.php/DianZhongNew/books");
        partnerUrl.setBookInfoUrl("http://www.pinyuew.com/api.php/DianZhongNew/bookInfo");
        partnerUrl.setChapterListUrl("http://www.pinyuew.com/api.php/DianZhongNew/chapters");
        partnerUrl.setChapterInfoUrl("http://www.pinyuew.com/api.php/DianZhongNew/chapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "249";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("47320dede393fcfe7d0dd24db1a5794b");
        owchPartner.setName("桃乐文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://api.taolewx.com/dianzhong/getbooklist");
        partnerUrl.setBookInfoUrl("http://api.taolewx.com/dianzhong/getbookinfo");
        partnerUrl.setChapterListUrl("http://api.taolewx.com/dianzhong/getvolumelist");
        partnerUrl.setChapterInfoUrl("http://api.taolewx.com/dianzhong/getchapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "250";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("57ae34c08f3aeee8d1ce29f66efd2d97");
        owchPartner.setName("阅下");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://webapi.yuexiawenhua.com/api/DZBook/GetBookList");
        partnerUrl.setBookInfoUrl("http://webapi.yuexiawenhua.com/api/DZBook/GetBookInformation");
        partnerUrl.setChapterListUrl("http://webapi.yuexiawenhua.com/api/DZBook/GetZhangList");
        partnerUrl.setChapterInfoUrl("http://webapi.yuexiawenhua.com/api/DZBook/GetzhangContentFor");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 253L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("0cd0852a00b7bd0447ba97e92c038f18");
        owchPartner.setName("联阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.qmydxs.com/apis/dianzong/BookList.php");
        partnerUrl.setBookInfoUrl("http://www.qmydxs.com/apis/dianzong/BookInfo.php");
        partnerUrl.setChapterListUrl("http://www.qmydxs.com/apis/dianzong/BookChapters.php");
        partnerUrl.setChapterInfoUrl("http://www.qmydxs.com/apis/dianzong/BookChapterInfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 254L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("910d12dd24260de8b61d423c69bbb6aa");
        owchPartner.setName("双溪文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.sxiwx.com/api/kuaikan/getBookList");
        partnerUrl.setBookInfoUrl("http://www.sxiwx.com/api/kuaikan/bookInfo");
        partnerUrl.setChapterListUrl("http://www.sxiwx.com/api/kuaikan/getChapters");
        partnerUrl.setChapterInfoUrl("http://www.sxiwx.com/api/kuaikan/chapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 257L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e1e4baddad2b782d3aa3f6c75277ea22");
        owchPartner.setName("马安");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://thirdapi.anmaa.com/api/dainzhong/books");
        partnerUrl.setBookInfoUrl("http://thirdapi.anmaa.com/api/dainzhong/Bookinfo");
        partnerUrl.setChapterListUrl("http://thirdapi.anmaa.com/api/dainzhong/chapters");
        partnerUrl.setChapterInfoUrl("http://thirdapi.anmaa.com/api/dainzhong/Chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 258L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("009e9a8a6450cb5ce4b53ac75674fe78");
        owchPartner.setName("牧文野读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://api.muwenyedu.cn/api/dianzhong/booklist");
        partnerUrl.setBookInfoUrl("https://api.muwenyedu.cn/api/dianzhong/bookinfo");
        partnerUrl.setChapterListUrl("https://api.muwenyedu.cn/api/dianzhong/chapterlist");
        partnerUrl.setChapterInfoUrl("https://api.muwenyedu.cn/api/dianzhong/chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 261L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("bfce39fcf16cf50f60fec73c291a8af6");
        owchPartner.setName("海阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.kaixin91.com/api.php/Dianzhong/booklist");
        partnerUrl.setBookInfoUrl("http://www.kaixin91.com/api.php/Dianzhong/bookinfo/book_id/{0}");
        partnerUrl.setChapterListUrl("http://www.kaixin91.com/api.php/Dianzhong/chapterlist/book_id/{0}");
        partnerUrl.setChapterInfoUrl("http://www.kaixin91.com/api.php/Dianzhong/chapterinfo/book_id/{0}/chapter_id/{1}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 262L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("9f01f593a586af34362ec0c609bd1b48");
        owchPartner.setName("风寰");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.fhchm.com/channel/dianzhong/getBookList");
        partnerUrl.setBookInfoUrl("http://api.fhchm.com/channel/dianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://api.fhchm.com/channel/dianzhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://api.fhchm.com/channel/dianzhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 263L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("4fc8f8e36367a2f5ff42b38a489d4b3d");
        owchPartner.setName("点阅");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.17qread.com/apis/dianzong/BookList.php");
        partnerUrl.setBookInfoUrl("http://api.17qread.com/apis/dianzong/BookInfo.php");
        partnerUrl.setChapterListUrl("http://api.17qread.com/apis/dianzong/BookChapters.php");
        partnerUrl.setChapterInfoUrl("http://api.17qread.com/apis/dianzong/BookChapterInfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 264L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("ab613b54afc7c9f3001de4e47329c23a");
        owchPartner.setName("啾咪");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.jiumibook.com/DianZhong/getBookList");
        partnerUrl.setBookInfoUrl("http://www.jiumibook.com/Home/DianZhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://www.jiumibook.com/Home/DianZhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://www.jiumibook.com/Home/DianZhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 265L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("65cb8a3636f29212f91df21700b3b948");
        owchPartner.setName("潮上月");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.ichaoshangyue.com/index.php/Api/ChaoDianZhong/booklist");
        partnerUrl.setBookInfoUrl("http://www.ichaoshangyue.com/index.php/Api/ChaoDianZhong/bookinfo/bookId/{0}");
        partnerUrl.setChapterListUrl("http://www.ichaoshangyue.com/index.php/Api/ChaoDianZhong/chapterlist/bookId/{0}");
        partnerUrl.setChapterInfoUrl("http://www.ichaoshangyue.com/index.php/Api/ChaoDianZhong/chapter/bookId/{0}/chapterId/{1}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 266L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("782f82785fcd47e7953bc578bc7d5214");
        owchPartner.setName("最爱原创网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.ziycw.com/api.php/DianZhongPush/getBookList");
        partnerUrl.setBookInfoUrl("http://www.ziycw.com/api.php/DianZhongPush/getBookInfo");
        partnerUrl.setChapterListUrl("http://www.ziycw.com/api.php/DianZhongPush/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://www.ziycw.com/api.php/DianZhongPush/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 267L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("5b0eb8cdf29e471abd0eec00a5bda655");
        owchPartner.setName("指尖阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://zjyd.lemengfun.com/dianzhong/getBookList");
        partnerUrl.setBookInfoUrl("http://zjyd.lemengfun.com/dianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://zjyd.lemengfun.com/dianzhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://zjyd.lemengfun.com/dianzhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 269L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("787fefe41072ae1dc8a4ff4eb9d97b53");
        owchPartner.setName("御书房");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://aibookchina.com/sqapi/dzapi/getBookList");
        partnerUrl.setBookInfoUrl("http://aibookchina.com/sqapi/dzapi/Chapterinfo");
        partnerUrl.setChapterListUrl("http://aibookchina.com/sqapi/dzapi/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://aibookchina.com/sqapi/dzapi/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 270L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("b12848cf08bc55cb93c7a3013bc279e6");
        owchPartner.setApiKey("fa44ba9726f2680066cbd21143eaf12b");
        owchPartner.setName("版权猫");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://api.banquanmao.com.cn/channel/custom/dianzhong/getbooklist?channelkey={0}&sign={1}");
        partnerUrl.setBookInfoUrl("https://api.banquanmao.com.cn/channel/custom/dianzhong/getbookinfo?channelkey={0}&sign={1}&book_id={2}");
        partnerUrl.setChapterListUrl("https://api.banquanmao.com.cn/channel/custom/dianzhong/getbookchapterlist?channelkey={0}&sign={1}&book_id={2}");
        partnerUrl.setChapterInfoUrl("https://api.banquanmao.com.cn/channel/custom/dianzhong/getbookchapter?channelkey={0}&sign={1}&book_id={2}&chapter_id={3}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 271L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("5fecd008c7f4b6e73fea9e80b7b93dc1");
        owchPartner.setName("白狐阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://open.fengxingss.com/api/getBookList");
        partnerUrl.setBookInfoUrl("https://open.fengxingss.com/api/getBookInfo");
        partnerUrl.setChapterListUrl("https://open.fengxingss.com/api/getVolumeList");
        partnerUrl.setChapterInfoUrl("https://open.fengxingss.com/api/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 273L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("1217");
        owchPartner.setApiKey("7b7e920e08d35d292c133ad19e5867a5");
        owchPartner.setName("花花中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.huahuaxs.com/?s=JsonApi&a=index&api=get.spbooks&spid={0}&outputformat=dianzhong&sign={1}");
        partnerUrl.setBookInfoUrl("http://www.huahuaxs.com/?s=JsonApi&a=index&api=get.book.info&spid={0}&outputformat=dianzhong&bookid={1}&sign={2}");
        partnerUrl.setChapterListUrl("http://www.huahuaxs.com/?s=JsonApi&a=index&api=get.book.chapter&spid={0}&outputformat=dianzhong&bookid={1}&sign={2}");
        partnerUrl.setChapterInfoUrl("http://www.huahuaxs.com/?s=JsonApi&a=index&api=get.book.content&spid={0}&outputformat=dianzhong&bookid={1}&chapterid={2}&sign={3}");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 274L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("fa1aa649da987eef52326ae14bf76674");
        owchPartner.setName("必看");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://sync.ibreader.com/coopApi/bookList");
        partnerUrl.setBookInfoUrl("http://sync.ibreader.com/coopApi/bookInfo");
        partnerUrl.setChapterListUrl("http://sync.ibreader.com/coopApi/chapters");
        partnerUrl.setChapterInfoUrl("http://sync.ibreader.com/coopApi/chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 276L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("4b26260d9b632b94c13154db9bb48e10");
        owchPartner.setName("忘忧书城");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.wangyou2.com/apis/dianzong/BookList.php");
        partnerUrl.setBookInfoUrl("http://api.wangyou2.com/apis/dianzong/BookInfo.php");
        partnerUrl.setChapterListUrl("http://api.wangyou2.com/apis/dianzong/BookChapters.php");
        partnerUrl.setChapterInfoUrl("http://api.wangyou2.com/apis/dianzong/BookChapterInfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 280L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("35a3ad37fd63e2b2b79332a1551600b9");
        owchPartner.setName("七猫中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://reader.wtzw.com/api/dianzhong/index?c=book&a=getBookList");
        partnerUrl.setBookInfoUrl("http://reader.wtzw.com/api/dianzhong/index?c=book&a=getBookInfo");
        partnerUrl.setChapterListUrl("http://reader.wtzw.com/api/dianzhong/index?c=chapter&a=getVolumeList");
        partnerUrl.setChapterInfoUrl("http://reader.wtzw.com/api/dianzhong/index?c=chapter&a=getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 283L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("c5263718c2d0e29e8dd90077a8916c2b");
        owchPartner.setName("九阅文化");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://cp.9yread.com/161/book_list");
        partnerUrl.setBookInfoUrl("http://cp.9yread.com/161/book_info");
        partnerUrl.setChapterListUrl("http://cp.9yread.com/161/chapter_list");
        partnerUrl.setChapterInfoUrl("http://cp.9yread.com/161/chapter_info");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 284L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("a712f61588e70d0f0e76a127721082d3");
        owchPartner.setName("拇指阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.muzhiyd.com/Api/Dianzhong/books");
        partnerUrl.setBookInfoUrl("http://www.muzhiyd.com/Api/Dianzhong/Bookinfo");
        partnerUrl.setChapterListUrl("http://www.muzhiyd.com/Api/Dianzhong/chapters");
        partnerUrl.setChapterInfoUrl("http://www.muzhiyd.com/Api/Dianzhong/Chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 285L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("807ba0a76fb2b0e2824121d09d83cdd4");
        owchPartner.setName("绾书文学网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://api.wanshu.com/dianzhong_interface/list");
        partnerUrl.setBookInfoUrl("https://api.wanshu.com/dianzhong_interface/info");
        partnerUrl.setChapterListUrl("https://api.wanshu.com/dianzhong_interface/chapters");
        partnerUrl.setChapterInfoUrl("https://api.wanshu.com/dianzhong_interface/content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 286L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("de88be87572d7b53d2af810f74d3c7ef");
        owchPartner.setName("萤火小说");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.yinghuoread.com/api/dianzhong/getBookList.do");
        partnerUrl.setBookInfoUrl("http://www.yinghuoread.com/api/dianzhong/getBookInfo.do");
        partnerUrl.setChapterListUrl("http://www.yinghuoread.com/api/dianzhong/getChapterList.do");
        partnerUrl.setChapterInfoUrl("http://www.yinghuoread.com/api/dianzhong/getChapterInfo.do");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 287L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("c588c46bc5c15139fc413edab93ec1e3");
        owchPartner.setName("连城读书");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.lc1001.com/dianzhong/book_list");
        partnerUrl.setBookInfoUrl("http://api.lc1001.com/dianzhong/book_info");
        partnerUrl.setChapterListUrl("http://api.lc1001.com/dianzhong/book_cata");
        partnerUrl.setChapterInfoUrl("http://api.lc1001.com/dianzhong/chapter_content");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 288L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("26d5d13dfc69e5598dd46bb7f12091ca");
        owchPartner.setName("听潮阁");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://39.100.119.110:8082/DzBook/getBookList?token=5a78bctc51baht204e9de16gp1o6d3t");
        partnerUrl.setBookInfoUrl("http://39.100.119.110:8082/DzBook/getBookInfo?token=5a78bctc51baht204e9de16gp1o6d3t");
        partnerUrl.setChapterListUrl("http://39.100.119.110:8082/DzBook/getVolumeList?token=5a78bctc51baht204e9de16gp1o6d3t");
        partnerUrl.setChapterInfoUrl("http://39.100.119.110:8082/DzBook/getChapterInfo?token=5a78bctc51baht204e9de16gp1o6d3t");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 291L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("6a8adfd6fdf51ca255a5915bbee9d1b9");
        owchPartner.setName("漫娱科技");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://coopsvr.mokayuedu.com/api/dianzhong/getBookList");
        partnerUrl.setBookInfoUrl("http://coopsvr.mokayuedu.com/api/dianzhong/getBookInfo");
        partnerUrl.setChapterListUrl("http://coopsvr.mokayuedu.com/api/dianzhong/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://coopsvr.mokayuedu.com/api/dianzhong/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 296L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("439c55ae6a568c09f595e8440ee5b6df");
        owchPartner.setName("晓恋悦读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.xiaolianyuedu.com/apis/dianzhong/articlelist.php");
        partnerUrl.setBookInfoUrl("http://www.xiaolianyuedu.com/apis/dianzhong/articleinfo.php");
        partnerUrl.setChapterListUrl("http://www.xiaolianyuedu.com/apis/dianzhong/articlechapter.php");
        partnerUrl.setChapterInfoUrl("http://www.xiaolianyuedu.com/apis/dianzhong/chaptercontent.php");
        owchPartner.setUrl(partnerUrl);*/

        Long clientId = 299L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("5b60f3024031bdd43d94bf245d29445b");
        owchPartner.setName("四维文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.siweiip.com/api/dianzhong/book_list.php");
        partnerUrl.setBookInfoUrl("http://www.siweiip.com/api/dianzhong/book_info.php");
        partnerUrl.setChapterListUrl("http://www.siweiip.com/api/dianzhong/chapter_list.php");
        partnerUrl.setChapterInfoUrl("http://www.siweiip.com/api/dianzhong/chapter_content.php");
        owchPartner.setUrl(partnerUrl);

        /*Long clientId = 300L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e4c0e45cbd675fb494dfed0bf015fc4a");
        owchPartner.setName("千马中文网");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.qmcmw.com/api/dianzhong/getBookList.php");
        partnerUrl.setBookInfoUrl("http://www.qmcmw.com/api/dianzhong/getBookInfo.php");
        partnerUrl.setChapterListUrl("http://www.qmcmw.com/api/dianzhong/getVolumeList.php");
        partnerUrl.setChapterInfoUrl("http://www.qmcmw.com/api/dianzhong/getChapterInfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 303L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("e9214138973ec0226dc24b69141abc7d");
        owchPartner.setName("一一阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("https://yiyiread.com/api/dianzhong/booklist.php");
        partnerUrl.setBookInfoUrl("https://yiyiread.com/api/dianzhong/bookinfo.php");
        partnerUrl.setChapterListUrl("https://yiyiread.com/api/dianzhong/chapterlist.php");
        partnerUrl.setChapterInfoUrl("https://yiyiread.com/api/dianzhong/chapterinfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 329L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("409bbbfdd9a840d11c42af1f63066216");
        owchPartner.setName("四季文学");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://www.sijiwenxue.com/apis/dianzong/BookList.php");
        partnerUrl.setBookInfoUrl("http://www.sijiwenxue.com/apis/dianzong/BookInfo.php");
        partnerUrl.setChapterListUrl("http://www.sijiwenxue.com/apis/dianzong/BookChapters.php");
        partnerUrl.setChapterInfoUrl("http://www.sijiwenxue.com/apis/dianzong/BookChapterInfo.php");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 336L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("588d0aca3271a7779caf6f0b20ad31b9");
        owchPartner.setName("广州铁读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.open.tiedu.com/api/getBookList");
        partnerUrl.setBookInfoUrl("http://api.open.tiedu.com/api/getBookInfo");
        partnerUrl.setChapterListUrl("http://api.open.tiedu.com/api/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://api.open.tiedu.com/api/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 100377L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("4fc8f8e36367a2f5ff42b38a489d4b3d");
        owchPartner.setName("弥汉书院");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://47.99.154.193/api/book/list");
        partnerUrl.setBookInfoUrl("http://47.99.154.193/api/book/info");
        partnerUrl.setChapterListUrl("http://47.99.154.193/api/chapter/list");
        partnerUrl.setChapterInfoUrl("http://47.99.154.193/api/chapter/info");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 100389L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("4fc8f8e36367a2f5ff42b38a489d4b3d");
        owchPartner.setName("弥汉书院");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://47.99.154.193/api/book/list");
        partnerUrl.setBookInfoUrl("http://47.99.154.193/api/book/info");
        partnerUrl.setChapterListUrl("http://47.99.154.193/api/chapter/list");
        partnerUrl.setChapterInfoUrl("http://47.99.154.193/api/chapter/info");
        owchPartner.setUrl(partnerUrl);*/

        /*String clientId = "11111111";
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId);
        owchPartner.setApiKey("85ee779ba3fd7bcc4ee64b344962234c");
        owchPartner.setName("北京娱阅读");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(Long.parseLong(clientId));
        partnerUrl.setBookListUrl("http://api.yuread.cn/dianzhong/books");
        partnerUrl.setBookInfoUrl("http://api.yuread.cn/dianzhong/bookInfo");
        partnerUrl.setChapterListUrl("http://api.yuread.cn/dianzhong/chapters");
        partnerUrl.setChapterInfoUrl("http://api.yuread.cn/dianzhong/chapterInfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 7443675L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("fsgrantdzh");
        owchPartner.setName("哎呦互娱");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://api.fensebook.com/index.php/Dianzhong/book?method=booklist");
        partnerUrl.setBookInfoUrl("http://api.fensebook.com/index.php/Dianzhong/book?method=bookinfo");
        partnerUrl.setChapterListUrl("http://api.fensebook.com/index.php/Dianzhong/book?method=chapterlist");
        partnerUrl.setChapterInfoUrl("http://api.fensebook.com/index.php/Dianzhong/book?method=chapterinfo");
        owchPartner.setUrl(partnerUrl);*/

        /*Long clientId = 30000003L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("cae22bb2c60129fab42734c9b400d62d");
        owchPartner.setName("中企瑞铭");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://cpaudio.geyueae.cn/api/DzAudio/getBookList");
        partnerUrl.setBookInfoUrl("http://cpaudio.geyueae.cn/api/DzAudio/getBookInfo");
        partnerUrl.setChapterListUrl("http://cpaudio.geyueae.cn/api/DzAudio/getVolumeList");
        partnerUrl.setChapterInfoUrl("http://cpaudio.geyueae.cn/api/DzAudio/getChapterInfo");
        owchPartner.setUrl(partnerUrl);*/
    }

    @Test
    public void getBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "231872");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "231872");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "39482", null, "2074372");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

    @Test
    public void compareBookList() throws Exception {
        Map<String, Integer> bookNamesDB = new HashMap<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/黑岩DB.txt"), "utf-8"));

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            bookNamesDB.put(row, 1);
        }

        System.out.println("数据库中数据数量：" + bookNamesDB.size());

        List<String> bookNameLocal = new ArrayList<>();
        List<String> bookNameNotExt = new ArrayList<>();

        // 授权书单
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("D:/黑岩书单.txt"), "utf-8"));

        while (true) {
            String row2 = br2.readLine();

            if (row2 == null) {
                break;
            }

            bookNameLocal.add(row2);

            if (!bookNamesDB.containsKey(row2)) {
                System.out.println(row2);
                bookNameNotExt.add(row2);
            }
        }

        System.out.println("书单书籍数量：" + bookNameLocal.size());
        System.out.println("未入库书单书籍数量：" + bookNameNotExt.size());
    }

    @Test
    public void printCategory() {
        //String url = "http://thirdapi.motie.com/api/yynovel/kuaikan/category/list?client_id=13&sign=87edd522734e103d7552424e470d525b"; // 13-逸云书院
        //String url = "http://thirdapi.motie.com/api/motie/kuaikan/category/list?client_id=75&sign=ad7ddf4fda8a6ebb9d635aad22cbfb4e"; // 18-磨铁数盟原创
        //String url = "http://api.hongshu.com/empower4/dianzhong/categorylist.php?client_id=40&sign=13ba66c6973af7173a7529362ba9ccde"; // 40-红薯网
        //String url = "http://91baby.mama.cn/cp/third/dianzhong/getCateList?client_id=49&sign=75fbd239c0bc01ab2aeb508e95cb0d8b"; // 49-阅书小说网
        //String url = "http://cp.api.luochen.com/interface/Dianzhong/categoryList?client_id=58&sign=e3615db3935fea94253f64f296aaacbd"; // 58-落尘文学
        //String url = "http://authbook.91yuedu.com/91yuedu/PointCrowd/categoryList?client_id=59&sign=99865e383e903ddeb3aca9e9c3bcf90a"; // 59-景像文学
        //String url = "http://bcpapi.tingyuxs.com/api/dianzhong/getcatelist?client_id=92&sign=3d8413c6365c21c112240db556f18358"; // 92-圣诞文学网
        //String url = "http://open.qingoo.cn/v1/dianzhong/categoryList?client_id=99&sign=6903dcf78732dbf656b499bdaf01303d"; // 99-青果阅读
        //String url = "http://www.ytread.com/api/zhihao/getCateList.php"; // 120-阅庭书院
        //String url = "http://openapi.tadu.com/dz/getCateList/?client_id=dezeal&sign=f0b9c1119a7b1ad3ccf3b3f422442018"; // 129-塔读文学
        //String url = "http://thirdapi.motie.com/api/zuitang/kuaikan/category/list?client_id=134&sign=23f255db93fbafcb82cee89fe1874976"; // 134-醉唐中文网
        //String url = "http://api.jinhuyu.cn/dianzhong/category?client_id=138&sign=ed8d813aead1650bce252003a3d7a0f7"; // 138-十音文学
        //String url = "http://bookapi.ledu18.cn/api/dianzhong/category?sign=a6a2a71ef0a36b3f66ed8a532f3438dc"; // 157-红书汇
        //String url = "http://www.wenduread.com/apis/dianzhong/getCateList.php?client_id=217&sign=4ec023a02db425071bfba219eb46c427"; // 217-温度书城
        //String url = "http://www.Lzbook.com/api/dots/category-list?client_id=226&sign=aa880233a88206f43666dab718c73bd1"; // 226-连众文学
        //String url = "http://www.qjread.com/apis/dianzhong/getCateList.php?client_id=242&sign=78e7d7ecb57f84ca6bd24ba376723edd"; // 242-青椒阅读
        //String url = "http://www.qmydxs.com/apis/dianzong/BookSort.php";// 253-联阅
        //String url = "http://www.kaixin91.com/api.php/Dianzhong/category";// 261-海阅
        //String url = "http://api.fhchm.com/channel/dianzhong/getCateList?client_id=262&sign=3c51949cec8d25bfa7dd196fdc1553aa"; // 262-风寰
        //String url = "http://api.17qread.com/apis/dianzong/BookSort.php"; // 263-点阅
        //String url = "http://www.jiumibook.com/DianZhong/getCateList?client_id=264&sign=2c6b6deb13c5790d66b4d0d0c9337311"; // 264-啾咪
        //String url = "http://www.ichaoshangyue.com/index.php/Api/ChaoDianZhong/categoryList"; // 265-潮上月
        //String url = "http://www.ziycw.com/api.php/DianZhongPush/getCateList?client_id=266&sign=b8806b712b48140dc6bb93788c94f2fc"; // 266-最爱文学网
        //String url = "http://zjyd.lemengfun.com/dianzhong/getCateList?client_id=267&sign=96ed2e831f76cecf828dc6454feee40f"; // 267-指尖阅读
        //String url = "http://aibookchina.com/sqapi/dzapi/getCateList?client_id=269&sign=102337d3414198af4b7210de90201381"; // 269-御书房
        //String url = "https://api.banquanmao.com.cn/channel/custom/dianzhong/getbookclass?channelkey=b12848cf08bc55cb93c7a3013bc279e6&sign=ff9738c5cf67666e329dd34ed9517b10"; // 270-版权猫
        //String url = "https://open.fengxingss.com/api/getCateList?client_id=271&sign=c5a1d736ad17e9b604130a0ecf6e06b1"; // 271-白狐阅读
        //String url = "http://www.huahuaxs.com/?s=JsonApi&a=index&api=get.book.sort&spid=1217&outputformat=dianzhong&sign=0c2823aa22bd4cf82f487a357802a9ba"; // 273-花花中文网
        //String url = "http://sync.ibreader.com/coopApi/categoryList?client_id=274&sign=b33e9dcc89f4638743f370e0e8271f4d"; // 274-必看
        //String url = "http://www.shierbook.com/api/dianzhong/getCateList.php?client_id=278&sign=0e8471a5e6bb41408c426bf6c5cc7770"; // 278-十二阅读
        //String url = "http://reader.wtzw.com/api/dianzhong/index?c=category&a=getCateList&client_id=280&sign=6e8adc0646ab75f9b286b625d4f33947"; // 280-七猫中文网
        //String url = "http://www.haigew.com/module/api/dianzhong.php?t=categorylist&sign=0f87c84f16b6d7b5ef1d9587c6661f9c&client_id=281"; // 281-海阁文化
        //String url = "http://cp.9yread.com/161/category_list?client_id=283&sign=9458ebf691e0a5ea9cc6ffd92d1a42c0"; // 283-九阅文化
        //String url = "http://www.muzhiyd.com/Api/Dianzhong/categoryList?client_id=284&sign=db36d4fcce10d6a6c513395f7e905b85"; // 284-拇指阅读
        //String url = "http://www.yinghuoread.com/api/dianzhong/getBookTypeList.do?client_id=286&sign=ece56cc8c66f0b8e431cb8e0b2138fb4"; // 286-萤火小说
        //String url = "http://39.100.119.110:8082/DzBook/getCateList?token=5a78bctc51baht204e9de16gp1o6d3t"; // 288-听潮阁
        //String url = "http://api.mubenbook.com/api/dianzhong/getCateList?client_id=289&sign=4dd1745a31ee38cf42d21eefdea21706"; // 289-木本水源
        //String url = "http://ly12345.xyz/api/c/dianzhong/getCateList?client_id=290&sign=379b0d1663b7817df7bde42d77360e9b"; // 290-蓝阅读
        //String url = "http://coopsvr.mokayuedu.com/api/dianzhong/getCateList?client_id=291&sign=5edb55c45ce1f98cc0292a54e9c34cbf"; // 291-漫娱科技
        //String url = "http://ext.cdyt.com/ch/dianz/getCateList.php"; // 293-成都悦听
        //String url = "http://www.xiaolianyuedu.com/apis/dianzhong/sort.php"; // 296-晓恋悦读
        //String url = "http://www.siweiip.com/api/dianzhong/category.php?client_id=299&sign=62c8e8178814d6a4e62ca47596a45112"; // 299-四维文学
        //String url = "http://www.qmcmw.com/api/dianzhong/getCateList.php?client_id=300&sign=e8909f6087b5227e23ae9675c504a5a9"; // 300-千马中文网
        //String url = "https://api.lianzai.com/api/service/dianzhong/getCateList?client_id=USER146121928707&sign=14fb98be63a5dd6b3ea3d887cf359f5f"; // 301-连载阅读
        //String url = "http://api.lanyueread.com/dianzhong/classlist.aspx?client_id=302"; // 302-揽月中文网
        //String url = "https://yiyiread.com/api/dianzhong/catelist.php?client_id=303&sign=a9a9bf19eb10b766b1f3b00c7a5f09fc"; // 303-一一阅读
        //String url = "http://223.99.225.28:8080/external/category.m?client_id=304&sign=c29411d721ddb5aea191b4d6d8b595e2"; // 304-宜搜
        //String url = "http://www.jinliread.com/apis/dianzong/BookSort.php"; // 305-西橙文化
        //String url = "http://www.jinyuezhongwen.com/api.php/Dianzhong/categorylist?client_id=308&sign=340d4566ce708753260f7ff079a931b3"; // 308-锦阅
        //String url = "http://www.htread.com/DianZhong/getCateList?client_id=309&sign=0c2de24ffdb8a44ca459560743cee3c0"; // 309-海豚阅读
        //String url = "http://admin.yubook.cn/api/dianzhong/getcategorylist?client_id=311&sign=17955eb79a8e2b491eee5f8e75cb4c99"; // 310-星雁文化
        //String url = "http://admin.yubook.cn/api/dianzhong/getcategorylist?client_id=311&sign=17955eb79a8e2b491eee5f8e75cb4c99"; // 311-大鱼快读
        //String url = "http://112.74.114.207/dianzhong/categoryList?client_id=310&sign=1d5373a50a6d216f65dd1fab049591d1"; // 312-紫薇文学
        //String url = "http://www.ziweiread.com/api.php/Dianzhong/categorylist?sign=a9c54d80ac5e05063e1f0e7cc7deadf3"; // 312-紫薇文学
        //String url = "http://www.zhongdubook.com/apis/dianzhong/getCateList.php?client_id=313&sign=1e6832f95ac35ea666ba8fe503a2e31a"; // 313-众读
        //String url = "http://www.fantangxs.com/channel/DianZhong/getCateList?client_id=314&sign=ff7e7886c3344bbad962b437cd136b62"; // 314-翻糖小说
        //String url = "http://api.iuban.com/dianzhong/category?client_id=316&sign=81c5029bbeb46778f6356fc08dbbadca"; // 316-有伴小说
        //String url = "http://www.yunxingzw.com/api/dz/categoryList?client_id=318&sign=023af0b7f4e7daa54bba58aba304c4c3"; // 318-云行中文网
        //String url = "http://opensk.49xiaoshuo.com/dianzhong/book/categorylists?client_id=319&sign=9044c26ba9888d078ce1e88260d9393d"; // 319-友和
        //String url = "http://fenghua.620309.com/api.php/Dianzhong/categorylist?sign=5aff2e234bd4e8bc47bc09da7e9df3e4"; // 321-煊煌文化
        //String url = "https://api.doufu.la/openapi/dianzhong/getCateList?client_id=322&sign=76ee405648b807e7624aa2bcbe3b91a7"; // 322-豆腐阅读
        //String url = "http://cp.jzxs.com/api/dianzhong/category?sign=e578e6f544b42058deb217927272a892"; // 323-九州小说
        //String url = "http://www.lanzeshuyuan.com/apis/dianzhong/getCateList.php?client_id=324&sign=efb91ea2535bacc061dd910a3051180f"; // 324-兰泽书院
        //String url = "http://channel.huaxi.net/DianZhong/categoryList?client_id=325&sign=546903f29b0e8d9b340cbfd5ebbdf5b2"; // 325-花溪小说
        //String url = "http://api.hongshu.com/empower4/dianzhong/categorylist.php?client_id=40&sign=13ba66c6973af7173a7529362ba9ccde"; // 326-红薯网（海外）
        //String url = "http://hxwxc.com/partners/DianzhongApi/categories?client_id=327&sign=5cca3c25f07ae834b9c15634e2129d0c"; // 327-红象文学
        //String url = "https://service.bmsgzw.cn/api/getCateList?client_id=328&sign=72e39d115c23d7347bea86a349b0f4d4"; // 328-白马时光中文网
        //String url = "http://www.sijiwenxue.com/apis/dianzong/BookSort.php"; // 329-四季文学
        //String url = "http://www.xiangguayuedu.cn/apis/dianzong/BookSort.php"; // 331-香瓜阅读
        //String url = "http://www.zhumengwx.com/apis/dianzhong/getCateList.php?client_id=333&sign=80da0de6770314e20d464a62f5cebcac"; // 333-筑梦文学
        //String url = "http://api.xiang5.com/dianzhongnew/categoryList"; // 334-大麦中金
        //String url = "https://cp.foreader.com.cn/dzh/api/getCateList?client_id=335&sign=89b8e0683a4eab181f4542385a8812bc"; // 335-速更小说
        //String url = "http://api.open.tiedu.com/api/getCateList?client_id=336&sign=325f6afb63b5df725c1d1f5e3d47b26a"; // 336-广州铁读
        //String url = "http://dianzhong.com.api.kandzww.com/json/cats?client_id=337"; // 337-看点文化
        //String url = "http://api.lizhiread.com/dianzhong/getCateList?client_id=338&sign=86cbf3a767e5945bc522487a72b6a298"; // 338-荔枝阅读
        //String url = "http://mojwx.com/Apidianzhong/getCateList?client_id=340&sign=26cbec446f04282b7ad2436652700589"; // 340-斐然成章
        //String url = "http://open.api.zhizihuan.com/action/novel/dianzhong/getCateList?client_id=341&sign=e010c09d775f7f8c0eb69a1724e5d003"; // 341-栀子欢
        //String url = "http://api.xinyuexsw.com/api/dianzhong/getCateList.html?client_id=342&book_id=1&chapter_id=18&sign=4f8711682387758e909b9cec04ccaf00"; // 342-暖阅小说网
        //String url = "http://www.chengzi.video/apis/dianzong/?method=sort&client_id=343&sign=9d0dae28823409d31b617f0df646a0f8"; // 343-桐年牧棉
        //String url = "http://www.ziyunsc.com/module/api/dianzhong.php?t=category&client_id=344&sign=cb4d87abb6d536a6241b56a77eb9ed32"; // 344-紫韵书城
        //String url = "http://www.huodiandushu.com/apis/dianzhong/getCateList.php?client_id=345&sign=3ba8771e3f710348fb4e7deedf9622b0"; // 345-火点文化
        //String url = "http://api.feigeng.net/index/Index/categoryList?client_id=346&sign=3a0dfc40470c564732686d52d1881479"; // 346-智汇创融
        //String url = "http://www.qirewx.com/api.php/Dianzhong/category?sign=98acc89060b2176a16ec852f599bd202"; // 348-嵩嘉文化
        //String url = "https://stack.haiduxs.com/api/dzbooks/categoryList?client_id=349&sign=cc5171e1577e3da4a363aaae1c12c370"; // 349-上海拓畅
        //String url = "http://www.xsdwxw.com/api.php/Dianzhong/category?sign=b9d7740e46af7d7512a3aa3081e5829c"; // 351-新生代文学网
        //String url = "http://output.qilibook.com/dianzhong/cate_list?sign=759ed68a18bce91dbab4d04509fb7e9e"; // 352-寄远文化
        //String url = "http://www.datangzww.com/api/empower_dz/siteGetBkCategory.php?client_id=358&sign=2fbc8f868549540c990f41b7e14ddd66"; // 358-杭州瑶池
        //String url = "https://distribution.sqreader.com/api/getCateList?client_id=359&sign=fce62cd3c286d3b81b42dce092202caf"; // 359-橙瓜阅读
        //String url = "https://original-distribution-interface.cread.com/dianzhong/pullMsg/categoryList?client_id=360&sign=2229a13fb4c51c6e7cdcbf102427e095"; // 360-中文万维
        //String url = "http://api.7louzw.com/dianzhong/category?client_id=361&sign=b2eeaf56035ef9d066ccac0c83a06124"; // 361-7楼中文网
        //String url = "http://api.tiedzw.com/channel/custom/dianzhong/getbookclass?channelkey=c71f37bd53c879500cde5b6d28eaf8fe&sign=49e773f58db1892739ba4cb1dfda1c22"; // 362-微阅数盟
        //String url = "https://api.content.xsycps.com/dianzhongniujiao/api/getCateList"; // 363-牛角科技
        //String url = "http://guyunxs.com/api/classifyList_dz?client_id=365"; // 365-谷韵文学
        //String url = "http://www.hongruxs.com/api/getCateList?client_id=366&sign=a7afcd594052d7adce9444d26a5d8d25"; // 366-弘儒文化
        //String url = "http://quyue.api.hzquyue.com/index.php/dianZhong/getCateList?client_id=367&sign=4c163f530a46ac9140c3680af36ec5e9"; // 367-杭州趣阅
        //String url = "http://www.qiyunwenxue.com/api.php/Dianzhong/category?client_id=368&sign=6cfb91f85d1dbd0862d8c5e22f9d8fc6"; // 368-启云文学
        //String url = "http://42.51.17.45:8084/api/getCateList?client_id=369&sign=a688a82da52ecab09e5daeb5f6dd9177"; // 369-疯行中文网
        //String url = "http://open.kxs5.com/api/dianzhong/category?client_id=370&sign=59745241f07af479f1b31968fb3e2fba"; // 370-光速映画
        //String url = "http://api.vvbook.cn/dianzhong/cate_list?client_id=371&sign=9753adb1959d65fa590c49ff4237df44"; // 371-青空之蓝
        //String url = "http://data.quduw.com/api/dianzhong/getCateList?client_id=372&sign=2901a45dc4eaf4bcbf77f6902af9edf6"; // 372-成都趣读
        //String url = "https://oauth.zhiyuewenxue.com/dianzong/catelist?client_id=373&sign=65d62c3b70aeba83cdee492a532f0fc6"; // 373-智阅文化
        //String url = "http://openapi.iyoule.com/dianzhongcp/category?client_id=374&sign=459c4b82c19005d0f5609734d2648d36"; // 374-有乐分成
        //String url = "http://tangguaxsw.com/module/api/dianzhong.php?t=getCateList"; // 375-糖瓜小说
        //String url = "http://api.yuanqimeng.com/book/categorylist?client_id=376&sign=87f7bedad298e8817642633fba0641c4"; // 376-元气萌
        //String url = "http://www.qianghuaer.com/apis/dianzhong/getCateList.php?client_id=377&sign=621e1fe016a3718992eeb56652ab180c"; // 377-枪花文学
        //String url = "http://api.sixiwenxue.com/?c=dianzong&a=category_list&client_id=378&sign=60d87425011d9ec01e5e0b70002d5e65"; // 378-四喜
        //String url = "https://www.f5read.com/api/dz/categorys?client_id=379&sign=0b1f4f4b16bf80ed9877ce9fb4762d7b"; // 379-粉雾文学
        //String url = "http://zhuishu.bdzww.com:899/dianzhong/getCateList?client_id=381&sign=de6a9659d1d0c02923288d5ed9a33548"; // 381-北斗
        //String url = "http://www.mengread.cn/api/dianzhong/getCateList.php?client_id=382&sign=25d90e57b0ec1e30206831ec597a9f91"; // 382-心语梦
        //String url = "https://oauth.xinyue6.com/dianzong/catelist?client_id=383&sign=2ea3874e227a5856efae76c48795a56f"; // 383-薪阅文化
        //String url = "http://www.hmrxs.cn/apis/dianzong/?method=sort&client_id=384&sign=10b4d0688d88c587d9f85932dac8d49e"; // 384-画美人小说
        //String url = "http://www.feiread.com/api.php/Dianzhong/category?sign=0634ad6fc6a36e645599d185b299bfae"; // 385-飞阅文化
        //String url = "http://www.muxiaoapi.com/openapi/dianzhong/getCateList?client_id=387&sign=cef11f7dac3a3d324c5af2ccadde5d9e"; // 387-博宇阅读
        //String url = "http://www.lanmaoyd.com/api/dianzhong/getCateList.php?client_id=388&sign=8eb51e1546872555fb6e3c6c050d5d5f"; // 388-懒猫阅读网
        //String url = "https://admin.lanmaoyd.com/api/channelApi/getCateList"; // 388-懒猫阅读网-新
        //String url = "http://www.zhixinbook.cn/api/dianzhong/category.php?client_id=389&sign=0af95dc8e52f6877651fa0634cacccea"; // 389-知心
        //String url = "http://app.icgread.com/dianzhong/getCategoryList?client_id=390&sign=468430031be231d8924ec1f13fc36500"; // 390-橙果
        //String url = "http://oauth.jingzhii.com/dianzong/catelist?client_id=391&sign=7e8d876a1bba41cf6bd0f900404496a0"; // 391-经致文学新站
        //String url = "http://www.feiyuyuedu.com/apis/dianzhong/getCateList.php?sign=2e0c3d94c542a1b671bc7ba51631b8c5&client_id=392"; // 392-飞鱼
        //String url = "http://dianzhong.xswenxue.com/novel/category/?client_id=10007&sign=ecffb15cb4e2f7d08f6051165988edad"; // 394-象森文化
        //String url = "http://data.jxwxu.com/api/dianzhong/getCateList?client_id=395&sign=6aeea92f6052ad573ed7015128466cb3"; // 395-凌欢
        //String url = "http://api.liulii.com/api/dianzhong/categorylist?client_id=396&sign=975fb030b760818e165343a536ba42fe"; // 396-意书
        //String url = "http://oauth.zhongyi6.com/dianzhong/catelist?client_id=397&sign=69c6b2e6c3e411b2a4baf687fe1c6db9"; // 397-中意
        //String url = "http://www.liwanyuedu.com/Interface/Dianzhong/categoryList?client_id=399&sign=42157a876738f4f10c74c588a5f817ff"; // 399-立玩
        //String url = "http://oauth.luyuedu.com/dianzong/catelist?client_id=400&sign=76a5412179f0cb145b7bc8289b2684d4"; // 400-鹿阅读
        //String url = "https://api.yuanyuangungun.com/dz/categoryList?client_id=401&sign=748fa4de60d45082de43162b28fd7adc"; // 401-源源滚滚
        //String url = "http://bookapi.fensebook.com/index.php/DianzhongCp/getCateList?client_id=402&sign=4b6b41cb78d2f09314192cfeefcf4772"; // 402-美丽气泡
        //String url = "http://www.skydzww.com/channel/dianzhng005/getCategorys?client_id=404&sign=d00960f113acf6edb8d8297d88d9e889"; // 404-时刻阅读
        //String url = "http://www.mokewx.com/api/dianzhong/getCateList.php?client_id=406&sign=e5f78f82ff6f3d0817a2993ed7735e36"; // 406-墨客
        //String url = "http://www.weiwanread.com/export/dz.php?key=dz38965e888111a3813b92318&type=cate"; // 407-未晚科学
        //String url = "http://www.haoyuewenxue.com/apipro/getCateList?client_id=81557511&sign=d2de89cf19192a1c43345414ae026430"; // 408-豪阅
        //String url = "http://export.quwen.online/index.php/dz/getCateList?client_id=409&sign=5be43020a16d484321b26c362525f2d4"; // 409-趣文
        //String url = "http://api.yanxiangbook.com/apibook/dianzhong/categoryList?client_id=LWSlvWU1qg1u1UgngiN5n43ATB45DyQTzrLXZC4XCfFFNaVyfesbif7jgvOjsHZP&sign=39ead2ea68c051cd57ffd9e7dea53f27"; // 410-悠书
        //String url = "http://oauth.xuantangxiaoshuo.com/dianzong/catelist?client_id=412&sign=40aa61a4e5cd1a496e5830f3fceb8b78"; // 412-炫糖小说
        //String url = "http://www.fengx100.com:8084/dianzhong/getCateList?client_id=413&sign=cd2b58ea1982520460cfd2791e789324"; // 413-疯行买断
        //String url = "http://output.moqing.com/api_dianzhong/getCateList?client_id=414&sign=0c44e0313e82358eb253199adda282c7"; // 414-魔情
        //String url = "http://api.dianyue.cn/api/dianzhong/getCateList?client_id=415&sign=5f6059d8a578316f9875f40ff0aeff6f"; // 415-北京点阅
        //String url = "https://easy-read.yiduwenhua.cn/api/dianzhong/categoryList?client_id=sOV4Y~hni4xopnhu,HxUiw2k2KLI2600IjX281g__"; // 416-易读文化
        //String url = "http://www.chengzi.video/apis/dianzong/?method=sort&client_id=418&sign=22945154ef1d721a741a124fac4d64b4"; // 418-书予文学
        //String url = "http://m.cmtread.com/apis/dianzhong/getCateList.php?sign=8422911eca7ae6d2951d5d3c395cefe0&client_id=419"; // 419-橙漫堂
        //String url = "http://oauth.sanmiao8.com/dianzong/catelist?client_id=420&sign=3d6da0503fbf8b030cfac8f5da9d2bd7"; // 420-三喵阅读
        //String url = "http://api.yxwenxue.com/channel/custom/dianzhong/getbookclass?channelkey=b37a5bb7b013597fdaf511421f8c891d&sign=b1b512827b296ec8087c00c24f8bb862"; // 421-银杏文学
        //String url = "https://www.jiangxinwenxue.com/dianzhong/getCateList?client_id=422&sign=dea894922e8805625125c4c62a030d2c"; // 422-匠心文学
        //String url = "http://m.qingyunread.com/api/dz/categorys?client_id=423&sign=9acbcca4ab5ea649eeebf62ad87cddb3"; // 423-青云阅读
        //String url = "http://www.shenaiyanqing.com/api/dianzhong/getCateList.php?client_id=425&sign=b35b9f6da11bdf6e72ca5fce21511f7a"; // 425-绘灵阁文化
        //String url = "http://api.feiyumedia.com/share-api/dianzhong/book/getCateList?client_id=426&sign=d2c718eb5a2369f42038450f416d6a6a"; // 426-飞阅读
        //String url = "http://www.7read.cn/apis/dianzhong/getCateList.php?client_id=427&sign=07b5ccea46050ed9dcac2e096ac3fa18"; // 427-蜗壳旗阅网络
        //String url = "https://www.mengmawenxue.com/api/dz/categorys?client_id=428&sign=e9c93f2e8ca1a6610805026ab90902b9"; // 428-猛犸文学
        //String url = "http://novel.voxpie.com/api.php/DzApi/categorylist?client_id=429&sign=52ca64f28773244580b156b97461cb1b"; // 429-中广影音
        //String url = "http://open.jiguangwx.com/dianzhong/categorylist?client_id=430&sign=dcffc0dfb4c0cede0036d67e7da34661"; // 430-吉光文学
        //String url = "http://www.qiyueread.com/apis/dianzhong/getCateList.php?client_id=431&sign=a8e5e53e1826b4722fb2fe8c2ff7271c"; // 431-启阅
        //String url = "http://cp.manmeng168.com/v1/output/dz/getCateList?sign=2497ccbc2f63c819370bae7e9a1ae84c&client_id=432"; // 432-番薯小说
        //String url = "http://api.91yuantuan.com/dianzhong/category?client_id=433&sign=7f5003bbea90dbbdd4344a1aebfcdef9"; // 433-圆团文化
        //String url = "http://api.bixinwx.com/api/getCateList?client_id=434&sign=123f4cbed4041cdb990b1f29594d581e"; // 434-笔芯文学
        //String url = "https://www.gaishiwenxue.com/api/dianzhong/categorys?sign=a0f75cdea53906a377adcc252922b547&client_id=435"; // 435-盖世中文网
        //String url = "http://output.maojiuxs.com/api_dianzhong/getCateList?client_id=436&sign=42b82d2abff15fbda9aa982a4fb5297a"; // 436-猫九小说
        //String url = "http://www.tianyuebook.com/apis/dianzhong/getCateList.php?client_id=437&sign=6dc7417d1f6abbef7bb66742eeb3e693"; // 437-阅看
        //String url = "http://oauth.yymmxxss.com/oauth/dianzong/catelist?client_id=438&sign=5ee5083a4fc8cb25ceaae32543b097e3"; // 438-言梦文学
        //String url = "https://www.shuzhong8.com/module/api/publish.php?action=category&channel=dianzhong&client_id=439&sign=1b29aadf1219adb04bc2300c001c74a1"; // 439-书中文化
        //String url = "https://api.laohufawei.com/bookstore/bookstore/interface/lhfw_dianzhong/categoryList.wds?client_id=440&sign=f524109787fea53d591605f9cf2eefbe"; // 440-老虎发威
        //String url = "http://output.tianbula.com/dianzhong/cate_list?sign=08992ba883df4efb3235d0b8045dcc52"; // 441-甜不辣
        //String url = "http://www.yundingxiaoshuo.com/api.php/Dianzhong/category?sign=2fac7824a89ed5a6f501d6c21409a83a"; // 443-云顶小说网
        //String url = "http://api.yongjiuwx.com/dianzhong/category?client_id=444&sign=c723046346726db44a762292106f6418"; // 444-永久文学
        //String url = "http://api.luworld.cn/dianzhong/category?client_id=448&sign=ca14f4b23aeadd02232e317bc30ed23c"; // 448-鹿世界
        //String url = "https://cp.reduzww.com/dianzhong/getCateList?client_id=449&sign=98d03344457d5b2bb2e91f06cd9144e0"; // 449-火文小说
        //String url = "https://output.moxiang5.com/dianzhong/cate_list?sign=96aab2e065814430e2442fef27c9a0cf"; // 450-抖喵
        //String url = "http://www.xishuiwx.com/export/dz.php?key=451f44c8234a14e0daaaf799c0ab39ef28c&type=cate"; // 451-溪水小说
        //String url = "https://thirdapi.motie.com/api/motie/dianzhong/categoryList?client_id=452&sign=ef265eabec466259d23f2755dd449d90"; // 452-磨铁
        //String url = "http://xyopen.8kana.com/dianzhong/categorylist?client_id=453&sign=ff16921ffb07bb9621a4ae7fb64c48c6"; // 453-溪阅小说
        //String url = "http://api.zhixinbook.cn/dianzhong/category?client_id=455&sign=29ec93c8e9c73a404b3b5d453b8d605b"; // 455-广州知心
        //String url = "http://cp.youmiydw.com/cp/BookSource?mcp=Dianzhong&method=categorylist&client_id=458&sign=4c2353b25f2a1f05d6dc9b4e48dee6b6"; // 458-有米阅读网
        //String url = "https://www.jiuhuaiwenxue.com/api/getCateList?client_id=459&sign=88616f82e065e2a601a5bea4592b7c65"; // 459-九怀中文网
        //String url = "https://www.kehaibook.com/index.php/api2/book/getCateList?client_id=460&sign=4fa2e76f2c61f7b214111e0971b51e89"; // 460-珂海文学
        //String url = "http://8.135.102.177:8303/dianzong/api/getCateList?sign=aeaded16bf48ede0fb1c6a95e80c8a5b"; // 461-番果文学
        //String url = "http://novel.ymlh.cn/api/book/getCateList?client_id=462&sign=caee62d924c775355aa35520962a44cd"; // 462-极致文学
        //String url = "http://open.365haoshu.com/dianzhong/getCateList.aspx?client_id=463&sign=2b1627c204ec2397199560ccaf7077a0"; // 463-平治
        //String url = "https://api-pc.muguaxiaoshuo.com/apiopen/dz/novel/pcsitecategory?client_id=464&sign=12a429296c1c58e233399fdd77d17076"; // 464-众阅
        //String url = "http://if.666read.com/Product/dianzhong/getCategoryList.aspx?client_id=465&sign=87924bf24ffc86fa720bc49faacd823b"; // 465-三界中文网
        //String url = "http://open.shooway.com/dianzhong/getCateList.html?client_id=466&sign=a59da0a3e7ff1fbe565de2bd20162204"; // 466-书唯文学
        //String url = "http://book.fengwork.com/api/book/getCateList?client_id=467&sign=5e7cbaee1824732df7236734a68ce7e4"; // 467-凤鸣互娱
        //String url = "http://oauth.xingyueks.com/dianzong/catelist?client_id=468&sign=7ec100d8cec330b8ee3ae5af7faaefbb"; // 468-星阅文化
        //String url = "https://anyo.fenfenyou.com/api/dianZhong/getCateList?client_id=469&sign=0cb49a50a77adaa91cfae9468449894f"; // 469-安阅
        //String url = "http://www.yhwh.vip/api.php/Dianzhong/categorylist?sign=762df0c058c9293f053a91d4c40a2287"; // 471-炎煌文化
        //String url = "http://oauth.bizhe8.com/dianzong/catelist?client_id=472&sign=67c022df2990d005adfea7760fa5558e"; // 472-讯读
        //String url = "http://pc.baiwei6.com/Apidianzhong/getCateList?client_id=473&sign=3fe3b3eb56df212635e87eb94e31888a"; // 473-江西环文
        //String url = "https://export.xinghuoyinqing.com/api/getCateList?client_id=xhmwt2cn&sign=fe0e4d807168cc8cd943aa8d0e8144d8"; // 474-银展科技
        //String url = "http://sds.beijzc.com/api/getBookTypeList?autograph=d2590c3ecf4172f5ac94514a0ad1bc26&client_id=10000&sign=76ca9cc8f9e1427a8fd9c188d8470ef1"; // 475-书山悦云
        //String url = "http://pc.cyycopyright.com/Apidianzhong/getCateList?sign=56801885e97c8eaaf9b7c10f446e5758"; // 476-浙江超元域
        //String url = "http://api.qiuqiuart.com/api/getCateList?client_id=477&sign=16a03a385c9a001b1bfb5fb2f4690770"; // 477-依米
        //String url = "http://api.tianhezw.com/dianzhong/getCateList?client_id=478&sign=5938d36b822d26f402fc1f4e71b08c21"; // 478-如星科技
        //String url = "http://ht-xs.bnvision.cn/api/dianzhong/getcategory?client_id=479&sign=87850301affc6046259418ab8d3bc418"; // 479-百年视觉
        //String url = "http://www.imepo.cn/api/server/channel/dianzhong/getCateList?client_id=480&sign=827815098003be88b8a2a1f9ad1eac01"; // 480-咪波文化
        //String url = "http://novel.atxg.world:4007/api/AuthorDianzhong/getCateList?client_id=20006&sign=f4ce76e1a77afe5fe78b744918c24ee9"; // 20006-谦译科技
        //String url = "http://oauth.penglairead.com/dianzong/catelist?client_id=20013&sign=3d3760f99e9f2ec6ebdb9080b2d25772"; // 20013-蓬莱中文网
        //String url = "http://cp.17k.com/dianZhongApi.do?mt=getCategoryList&client_id=20018&sign=7320ac779d27f1ded0c79ffd07a70e67"; // 20018-中文在线新接口
        //String url = "http://www.mengchang.com/dianzhong/getCateList?client_id=20027&sign=4684e055e6ef7e63699fe72d6fc6bcc1"; // 20027-掌读
        //String url = "http://api.zhishuwang.cn/api.php/dianZhong/getCateList?client_id=20031&sign=e1e8733d2557174f5fdcb195f072f57b"; // 20031-趣阅短篇
        //String url = "http://cp.17k.com/dianZhongApi.do?mt=getCategoryList&client_id=20033&sign=ff96a9e600b86c9247f006e2d17590d0"; // 20033-江南书城
        //String url = "http://yaoyao.pro/StackRoom/Api/dianzhong/GetCateList?client_id=20034&sign=23c748f85c21107bfa6a8f4f76efb274"; // 20034-夭夭文化
        //String url = "http://oauth.moyu3.com/dianzong/catelist?client_id=20036&sign=85c61574f05c9529a10e276082c77ba9"; // 20036-魔眼科技
        //String url = "http://www.daodao.top/BES/Api/dianzhong/getCateList?client_id=20054&sign=09d6fb60f71afcd85ac8f0d1848e7474"; // 20054-刀刀文化
        //String url = "http://oauth.fengyexs.com/dianzong/catelist?client_id=20068&sign=e49dcd966a9737496dc4e8a36a9b5f4b"; // 20068-枫叶小说
        //String url = "http://oauth.jiuzhe888.com/dianzong/catelist?client_id=20090&sign=2e9e37b1bbb08e5335b99a4ce7205583&book_id=11"; // 20090-就这文学
        //String url = "http://oauth.4mi.2cms.top/dianzong/catelist?client_id=20129&sign=4ac2a4850a1f4248ae129edac577b57b"; // 20129-铸神文化
        //String url = "http://sync.ibreader.com/dianzhongydApi/categoryList?client_id=20131&sign=cfb1a6e6b2061249dc3d77d0a78b9f10"; // 20131-简书
        //String url = "https://apiv2.hongshu.com/api/getCateList?client_id=21033&sign=bae9d8b313677b961afbb755cf5e86f9"; // 20133-南京红薯
        //String url = "https://service.dianjinnovel.com/api/getCateList?client_id=20146&sign=2ec71ed2d103400c5337f30a2f2c84e3"; // 20146-点金小说
        //String url = "http://api.zhulang.com/admin_a/main.php?app=dianzhonginterface&action=getCateList&client_id=20181&sign=f020963bf219d3408577a7b3cb149696"; // 20181-逐浪文学
        String url = "http://cp.17k.com/dianZhongApi.do?mt=getCategoryList&client_id=20201&sign=c27a8834a13be7d3adb8c672651e01c0"; // 20201-拂晓书城
        //String url = "http://ys.shayuu.com/api/dz/categorys?client_id=30000002&sign=e0cae870ad0a70305d9f9960d7b3a1c9"; // 30000002-傻鱼中文网（有声）
        String resp = HttpUtil.sendGet(url);
        //JSONArray jsonArray = JSON.parseObject(resp).getJSONArray("data");
        JSONArray jsonArray = JSON.parseArray(resp);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject.get("id") + "\t" + jsonObject.get("name"));
        }
    }

    @Test
    public void valid() {
        try {
            Map<String, Integer> bookNamesDB = new HashMap<>();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/阿里书籍列表.txt"), "utf-8"));

            while (true) {
                String row = br.readLine();

                if (row == null) {
                    break;
                }

                bookNamesDB.put(row.trim(), 1);
            }

            System.out.println("书籍数量：" + bookNamesDB.size());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeLine() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/王牌高手_11010028597.txt"), "utf-8"));
        List<String> contents = new ArrayList<>();

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            contents.add(row);
        }

        int idx = 0;

        for (int i = 0; i < contents.size(); i++) {
            String row = contents.get(i);

            if (row.startsWith("###")) {
                idx = i;
            }

            if (i == idx + 1) {
                contents.set(i, "");
                System.out.println("第" + (i + 1) + "行替换成功");
            }
        }

        FileWriter out = new FileWriter(new File("D:/11010028597.txt"));

        for (String content : contents) {
            out.write(content + "\n");
        }
        System.out.println("替换完毕");

        out.flush();
        out.close();
    }

    @Test
    public void createBookPath() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/sync/20191203-06.txt"), "utf-8"));
        FileWriter out = new FileWriter("D:/sync/20191203-06_book_path.txt");

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String bookPath = BookSupport.getBookPath(row);
            System.out.println(bookPath);
            out.write(bookPath + "\r\n");
        }

        br.close();
        out.close();
    }

    @Test
    public void printAllBookListUrl() throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/urls.txt")));
        List<Partner> partners = partnerMapper.listAllPartners(1);
        
        for (Partner partner : partners) {
            Long id = partner.getId();

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(id + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(id);
                instance.getBookList(partner);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = TraceKeyHolder.getUserKey("url");
            System.out.println(id + "\t" + url);
            writer.write(id + "\t" + url + "\n");
        }

        writer.close();
    }

    @Test
    public void convertCategory() throws Exception {
        List<Map<String, String>> typeTwoMapList = bookTypeTwoMapper.getAudioTypeTwoMap();
        List<Map<String, String>> typeThreeMapList = bookTypeTwoMapper.getAudioTypeThreeMap();

        Map<String, String> typeTwoMap = new HashMap<>();

        typeTwoMapList.forEach(p -> {
            typeTwoMap.put(p.get("name"), p.get("id"));
        });

        Map<String, String> typeThreeMap = new HashMap<>();
        typeThreeMapList.forEach(p -> {
            typeThreeMap.put(p.get("name"), p.get("id"));
        });

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/audio_category.txt")));
        FileWriter out = new FileWriter("D:/audio_category.sql");

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String[] split = row.split("\t");
            String bookId = split[0];
            String typeTwo = split[1];
            String typeThree = split[1] + "_" + split[2];
            String typeTwoNum = String.valueOf(typeTwoMap.get(typeTwo));
            String typeThreeNum = String.valueOf(typeThreeMap.get(typeThree));

            if (typeTwoMap.containsKey(typeTwo) && typeThreeMap.containsKey(typeThree)) {
                String sql  = "INSERT INTO `asg`.`b_book_type_center`(`book_id`, `one_type_id`, `two_type_id`, `three_type_id`, `ctime`, `utime`) VALUES ('" + bookId + "', 7, " + typeTwoNum + ", " + typeThreeNum + ", NOW(), NOW());";
                out.write(sql + "\n");
            } else {
                System.out.println(bookId + ", " + typeTwo + ", " + typeThree);
            }
        }

        br.close();
        out.close();
    }

    @Test
    public void compareGrade() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/songwj/Downloads/finalCompare.txt")));
        FileWriter out = new FileWriter(new File("/Users/songwj/Downloads/diff2.txt"), true);
        Map<String, String> mainGradeMap = new HashMap<>();
        Map<String, String> overseaGradeMap = new HashMap<>();
        while (true) {
            String row = br.readLine();
            if (row == null) {
                break;
            }
            if (StringUtils.isNotBlank(row)) {
                String[] splits = row.split("\t");
                mainGradeMap.put(splits[0], splits[1]);
                overseaGradeMap.put(splits[2], splits.length == 4 ? splits[3] : "");
            }
        }

        Set<Map.Entry<String, String>> entries = mainGradeMap.entrySet();
        for (Map.Entry<String, String> me : entries) {
            String mainBookId = me.getKey();
            String mainGrade = me.getValue();
            if (overseaGradeMap.containsKey(mainBookId)) {
                String overseaGrade = overseaGradeMap.get(mainBookId);
                if (!StringUtils.equals(mainGrade, overseaGrade)) {
                    System.out.println(mainBookId);
                    out.write(mainBookId + "\n");
                }
            } else {
                System.out.println(mainBookId);
                out.write(mainBookId + "\n");
            }
        }

        br.close();
        out.close();
    }

    @Test
    public void compareAudioBook() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/songwj/Downloads/database.txt"), "utf-8"));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/songwj/Downloads/interface.txt"), "utf-8"));
        Set<String> dbCpBookIds = new HashSet<>();
        Set<String> interfaceCpBookIds = new HashSet<>();

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            dbCpBookIds.add(row);
        }

        while (true) {
            String row = br2.readLine();

            if (row == null) {
                break;
            }

            interfaceCpBookIds.add(row);
        }

        for (String dbCpBookId : dbCpBookIds) {
            if (!interfaceCpBookIds.contains(dbCpBookId)) {
                System.out.println(dbCpBookId);
            }
        }

        br.close();
        br2.close();
    }

}