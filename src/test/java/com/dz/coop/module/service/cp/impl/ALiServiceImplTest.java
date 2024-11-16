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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author panqz 2018-12-16 8:46 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ALiServiceImplTest {

    @Resource(name="ALiServiceImpl")
    private ClientService clientService;

    private static Partner owchPartner = new Partner();

    static {
        /*Long clientId = 10008L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId(clientId.toString());
        owchPartner.setApiKey("f662d1d75b35d058efbf26fead874943");
        owchPartner.setName("阿里文学");*/

        Long clientId = 442L;
        owchPartner.setId(clientId);
        owchPartner.setAliasId("10088");
        owchPartner.setApiKey("fdbb11231a9d191d6d55538520c1dc10");
        owchPartner.setName("书旗小说");

        PartnerUrl partnerUrl = new PartnerUrl();
        partnerUrl.setId(clientId);
        partnerUrl.setBookListUrl("http://ognv1.shuqireader.com/cpapi/cp/booklist");
        partnerUrl.setBookInfoUrl("http://ognv1.shuqireader.com/cpapi/cp/bookinfo");
        partnerUrl.setChapterListUrl("http://ognv1.shuqireader.com/cpapi/cp/chapterlist");
        partnerUrl.setChapterInfoUrl("http://ognv1.shuqireader.com/cpapi/cp/content");
        owchPartner.setUrl(partnerUrl);
    }

    @Test
    public void testGetBookList() throws Exception {
        List<CPBook> cpBookList = clientService.getBookList(owchPartner);
        System.out.println(JsonUtils.toJSON(cpBookList));
    }

    @Test
    public void getBookInfo() throws Exception {
        CPBook cpBook = clientService.getBookInfo(owchPartner, "146435");
        System.out.println(JsonUtils.toJSON(cpBook));
    }

    @Test
    public void getVolumeList() throws Exception {
        List<CPVolume> cpVolumes = clientService.getVolumeList(owchPartner, "146435");
        System.out.println(JsonUtils.toJSON(cpVolumes));
    }

    @Test
    public void getCPChapterInfo() throws Exception {
        CPChapter cpChapter = clientService.getCPChapterInfo(owchPartner, "146435", null, "559688");
        System.out.println(JsonUtils.toJSON(cpChapter));
    }

    @Test
    public void valid() {
        try {
            List<String> bookNamesDB = new ArrayList<>();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/19本书.txt"), "utf-8"));

            while (true) {
                String row = br.readLine();

                if (row == null) {
                    break;
                }

                bookNamesDB.add(row.trim());
            }

            System.out.println("书籍数量：" + bookNamesDB.size());

            // 书籍列表
            List<CPBook> cpBookList = clientService.getBookList(owchPartner);

            Map<String, Object> map = new HashMap<>();

            for (CPBook cpBook : cpBookList) {
                map.put(cpBook.getName(), cpBook.getId());
            }

            for (String bookName : bookNamesDB) {
                if (map.containsKey(bookName)) {
                    String cpBookId = (String)map.get(bookName);

                    List<CPVolume> volumeList = null;

                    try {
                        volumeList = clientService.getVolumeList(owchPartner, cpBookId);
                    }  catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(cpBookId + "\t" + bookName + "，获取异常");
                        continue;
                    }

                    System.out.println(cpBookId + "\t" + bookName + "\t" + volumeList.get(0).getChapterList().get(0).getId() + "\t" +
                            volumeList.get(volumeList.size() - 1).getChapterList().get(volumeList.get(volumeList.size() - 1).getChapterList().size() - 1).getId());
                } else {
                    System.out.println("书籍在接口中不存在：" + bookName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}