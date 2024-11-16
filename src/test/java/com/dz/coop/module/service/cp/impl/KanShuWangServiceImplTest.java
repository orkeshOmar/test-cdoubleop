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
 * @author panqz 2019-02-25 10:12
 */
public class KanShuWangServiceImplTest extends TestBase {

    @Resource(name="kanShuWangServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        Long clientId = 100333L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("");
        owchPartner.setName("看书网-书香");
        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://hezuo.lunjian.com/open/kshzold/BookLists?cono={0}");
        partnerUrl.setBookInfoUrl("http://hezuo.lunjian.com/open/kshzold/BookDetail?cono={0}&bookid={1}");
        partnerUrl.setChapterListUrl("http://hezuo.lunjian.com/open/kshzold/ChapterLists?cono={0}&bookid={1}");
        partnerUrl.setChapterInfoUrl("http://hezuo.lunjian.com/open/kshzold/ChapterContent?cono={0}&bookid={1}&chapterid={2}");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "30160");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "30160");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "30160", null, "13721268");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

}