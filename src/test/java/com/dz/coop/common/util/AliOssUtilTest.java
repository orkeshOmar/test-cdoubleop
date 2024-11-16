package com.dz.coop.common.util;

import com.dz.coop.module.support.BookSupport;
import com.dz.coop.module.support.ChapterSupport;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @project: coop-client
 * @description: 阿里OSS工具类测试
 * @author: songwj
 * @date: 2020-04-22 11:24
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class AliOssUtilTest {

    static {
        System.setProperty("disk.switch", "on");
    }

    @Test
    public void testSetContent() throws Exception {
        String bookId = "11000000034";
        Long chapterId = 46521572L;
        String chapterPath = ChapterSupport.getChapterPath(bookId, chapterId);
        String content = "Nothing in the world is impossible, if you put your mind to do it!";
        AliOssUtil.setContent(chapterPath, content);
    }

    @Test
    public void testGetContent() throws Exception {
        String bookId = "11000000034";
        Long chapterId = 51299770L;
        String chapterPath = ChapterSupport.getChapterPath(bookId, chapterId);
        String content = AliOssUtil.getContent(chapterPath);
        System.out.println(content);
    }

    @Test
    public void testSetFile() throws Exception {
        String bookId = "30000000008";
        Long chapterId = 46521369L;
        String path = ChapterSupport.getAudioChapterPath(bookId, chapterId, ChapterSupport.AUDIO_CHAPTER_SUFFIX);
        InputStream is = new FileInputStream(new File("D:/data/cppartner/3x0/30x0/300x0/30000000008/46521369.m4a"));
        AliOssUtil.setFile(path, is);
    }

    @Test
    public void testSetFile2() throws Exception {
        String bookId = "30000000008";
        String path = BookSupport.getBookImg(bookId);

        URL url = new URL("http://oe3j032al.bkt.clouddn.com/cppartner/1x1/11x0/110x1/11010064016/11010064016.jpg");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        conn.connect();

        // 得到输入流
        InputStream is = conn.getInputStream();
        AliOssUtil.setCover(path, is);
    }

    @Test
    public void testGetCover() throws Exception {
        String bookId = "30000000008";
        String path = BookSupport.getBookImg(bookId);
        AliOssUtil.getFile(path);
    }

    @Test
    public void testGetFile() throws Exception {
        String bookId = "30000000008";
        Long chapterId = 46521369L;
        String path = ChapterSupport.getAudioChapterPath(bookId, chapterId, ChapterSupport.AUDIO_CHAPTER_SUFFIX);
        InputStream is = AliOssUtil.getFile(path);

        System.out.println("开始下载");
        long start = System.currentTimeMillis();
        DataInputStream dis = new DataInputStream(is);
        OutputStream out = new FileOutputStream(new File("D:/data/cppartner/3x0/30x0/300x0/30000000008/test.m4a"));

        byte[] bt = new byte[10240];
        int len = 0;

        while ((len = dis.read(bt)) != -1) {
            out.write(bt, 0, len);
        }

        System.out.println("文件下载完毕，耗时：" + (System.currentTimeMillis() - start) / 1000.0 + "秒，文件存放路径");
        is.close();
    }

    @Test
    public void testDownloadImg() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/glory_book_img_path.txt")));
        int count = 1;

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }


            URL url = new URL(row);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            String bookId = row.split("/")[7];
            String path = "D:/glory_img/" + bookId + ".jpg";
            FileUtils.copyInputStreamToFile(is, new File(path));
            System.out.println(count + " " + path);
            count++;
        }

        br.close();
    }

    @Test
    public void testFileMd5() throws Exception {
        // 现网NAS
        File file = new File("D:/image/0707_nas");
        File[] files = file.listFiles();

        // 华为CND
        File file2 = new File("D:/image/glory_img");
        File[] files2 = file2.listFiles();

        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(absolutePath));

            String absolutePath2 = files2[i].getAbsolutePath();
            String md5Hex2 = DigestUtils.md5Hex(new FileInputStream(absolutePath2));

            // 现网NAS上和华为CND比较不同
            if (!md5Hex.equals(md5Hex2)) {
                System.out.println(absolutePath + " → " + md5Hex + ", " + absolutePath2 + " → " + md5Hex2);
                FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath), new File("D:/image/_nas/" + files[i].getName() + ".jpg"));
                FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath2), new File("D:/image/_nas/" + files[i].getName()));
                FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath2), new File("D:/image/_final/" + files[i].getName()));
            }
        }

        // 6月份OSS
        File file3 = new File("D:/image/6_back");
        File[] files3 = file3.listFiles();

        for (File f3 : files3) {
            String absolutePath = f3.getAbsolutePath();
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(absolutePath));

            String path = "D:/image/0707_nas/" + f3.getName();
            String md5Hex2 = DigestUtils.md5Hex(new FileInputStream(path));

            // 6月份OSS和现网NAS比较不同
            if (!md5Hex.equals(md5Hex2)) {
                System.out.println(absolutePath + " → " + md5Hex + ", " + path + " → " + md5Hex2);
                FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath), new File("D:/image/_6_oss/" + f3.getName()));
                FileUtils.copyInputStreamToFile(new FileInputStream(path), new File("D:/image/_6_oss/" + f3.getName() + ".jpg"));
                FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath), new File("D:/image/_final2/" + f3.getName()));
            }
        }
    }

    @Test
    public void testCreateImagePath() throws Exception {
        File file = new File("D:/image/final_new_img");
        File[] files = file.listFiles();
        FileWriter out = new FileWriter("D:/image/refreshPath.txt");

        for (File f : files) {
            String absolutePath = f.getAbsolutePath();
            String name = f.getName();
            String bookId = name.split("\\.")[0];
            String bookPath = BookSupport.getBookPath(bookId, "/data/cppartner/") + name;
            FileUtils.copyInputStreamToFile(new FileInputStream(absolutePath), new File("D:/image" + bookPath));
            out.write(bookPath + "\n");
        }

        out.close();
    }

    @Test
    public void testDownloadChapterContent() throws IOException {
        String bookId = "11010072488";
        long[] chapterIds = {481258907L, 481258908L, 481258909L, 481258910L, 481258911L, 481258912L, 481258913L, 481258914L, 481258915L, 481258916L, 481258917L, 481258918L, 481258919L, 481258920L, 481258921L, 481258922L, 481258923L, 481258924L, 481258925L, 481258926L, 481258927L, 481258928L, 481258929L, 481258930L, 481258931L, 481258932L, 481258933L, 481258934L, 481258935L, 481258936L, 481258937L, 481258938L, 481258939L, 481258940L, 481258941L, 481258942L, 481258943L, 481258944L, 481258945L, 481258946L, 481258947L, 481258948L, 481258949L, 481258950L, 481258951L, 481258952L, 481258953L, 481258954L, 481258955L, 481258956L, 481258957L, 481258958L, 481258959L, 481258960L, 481258961L, 481258962L, 481258963L, 481258964L, 481258965L, 481258966L, 481258967L, 481258968L, 481258969L, 481258970L, 481258971L, 481258972L, 481258973L, 481258974L, 481258975L, 481258976L, 481258977L, 481258978L, 481258979L, 481258980L, 481258981L, 481258982L, 481258983L, 481258984L, 481258985L, 481258986L, 481258987L, 481258988L, 481258989L, 481258990L, 481258991L, 481258992L, 481258993L, 481258995L, 481258997L, 481258999L, 481259001L, 481259003L, 481259005L, 481259006L, 481259008L, 481259010L, 481259012L, 481259014L, 481259016L, 481259018L, 481259020L, 481259022L, 481259024L, 481259026L, 481259028L, 481259030L, 481259031L, 481259033L, 481259035L, 481259037L, 481259039L, 481259041L, 481259043L, 481259044L, 481259046L, 481259048L, 481259050L, 481259052L, 481259054L, 481259056L, 481259058L, 481259059L, 481259061L, 481259063L, 481259065L, 481259067L, 481259069L, 481259071L, 481259073L, 481259074L, 481259076L, 481259078L, 481259080L, 481259082L, 481259083L, 481259085L, 481259087L, 481259089L, 481259091L, 481259093L, 481259095L, 481259096L, 481259098L, 481259100L, 481259102L, 481259104L, 481259106L, 481259107L, 481259110L, 481259112L, 481259114L, 481259116L, 481259117L, 481259119L, 481259121L, 481259123L, 481259125L, 481259127L, 481259129L, 481259131L, 481259132L, 481259134L, 481259136L, 481259139L, 481259141L, 481259143L, 481259145L, 481259147L, 481259149L, 481259151L, 481259152L, 481259155L, 481259156L, 481259158L, 481259160L, 481259162L, 481259164L, 481259166L, 481259168L, 481259169L, 481259171L, 481259173L, 481259175L, 481259177L, 481259178L, 481259180L, 481259182L, 481259184L, 481259186L, 481259187L, 481259189L, 481259191L, 481259193L, 481259195L, 481259197L, 481259199L, 481259201L, 481259203L, 481259205L, 481259207L, 481259209L, 481259210L, 481259212L, 481259214L, 481259216L, 481259218L, 481259220L, 481259222L, 481259224L, 481259226L, 481259227L, 481259229L, 481259231L, 481259233L, 481259235L, 481259237L, 481259239L, 481259241L, 481259243L, 481259245L, 481259247L, 481259249L, 481259251L, 481259253L, 481259255L, 481259256L, 481259258L, 481259260L, 481259262L, 481259264L, 481259266L, 481259267L, 481259269L, 481259271L, 481259273L, 481259275L, 481259277L, 481259279L, 481259280L, 481259281L, 481259283L, 481259285L, 481259287L, 481259289L, 481259291L, 481259293L, 481259295L, 481259296L, 481259298L, 481259300L, 481259302L, 481259304L, 481259306L, 481259308L, 481259310L, 481259312L, 481259313L, 481259315L, 481259317L, 481259319L, 481259321L, 481259323L, 481259324L, 481259326L, 481259328L, 481259330L, 481259332L, 481259334L, 481259336L, 481259338L, 481259340L, 481259342L, 481259344L, 481259346L, 481259348L, 481259349L, 481259350L, 481259352L, 481259354L, 481259356L, 481259358L, 481259359L, 481303250L, 481303251L, 481303252L, 481303253L, 481324215L, 481324216L, 481324217L, 481324218L, 481324219L, 481436219L, 481436220L, 481436221L, 481436222L, 481436223L, 481464937L, 481464938L, 481464939L, 481464940L, 481464941L, 481464942L, 481464943L, 481464944L, 481488856L, 481488857L, 481488858L, 481488859L, 481488860L, 481488861L, 481510994L, 481510995L, 481510996L, 481510997L, 481510998L, 481510999L, 481511000L, 481527712L, 481527713L, 481527714L, 481527715L, 481527716L, 481527717L, 481549950L, 481549951L, 481549954L, 481549955L, 481549956L, 481549957L, 481561007L, 481561008L, 481561009L, 481561010L, 481583361L, 481583362L, 481583363L, 481583364L, 481630478L, 481630479L, 481727601L, 481727602L, 481727603L, 481727604L, 481727605L, 481727606L, 481744695L, 481744696L, 481744697L, 481744698L, 481744699L, 481744700L, 481770128L, 481770129L, 481770130L, 481770131L, 481770132L, 481782036L, 481782037L, 481791723L, 481791724L, 481863278L, 481863279L, 481863280L, 481863281L, 481863282L, 481863283L, 481935011L, 481935012L, 481935013L, 481935014L, 481935015L, 481935016L, 481981911L, 481981912L, 481998054L, 481998056L, 482022213L, 482022214L, 482022215L, 482022216L, 482022217L, 482022218L, 482046900L, 482046901L, 482046902L, 482046903L, 482046904L, 482046905L, 482070367L, 482070369L, 482070370L, 482070371L, 482070373L, 482070374L, 482082351L, 482082352L, 482082353L, 482082354L, 482116453L, 482116454L, 482116455L, 482116457L, 482116458L, 482116459L, 482116460L, 482116461L, 482358449L, 482358451L, 482358453L, 482358454L, 482358456L, 482358457L, 482395537L, 482395538L, 482395539L, 482395540L, 482395541L, 482395542L, 482453728L, 482453729L, 482453730L, 482454035L, 482454036L, 482454037L, 482475762L, 482475763L, 482475765L, 482475767L, 482475770L, 482475773L, 482492719L, 482492720L, 482492721L, 482492722L, 482492723L, 482492724L, 482516762L, 482516763L, 482516764L, 482516766L, 482516767L, 482516768L, 482563724L, 482563725L, 482563726L, 482563728L, 482563730L, 482563731L, 482563732L, 482563735L, 482605342L, 482605343L, 482605344L, 482605345L, 482605346L, 482605347L, 482632314L, 482632315L, 482632316L, 482632317L, 482632318L, 482632320L, 482677953L, 482677955L, 482677956L, 482677957L, 482677958L, 482677960L, 482697585L, 482697587L, 482697588L, 482697590L, 482697591L, 482697592L, 482705860L, 482705862L, 482705863L, 482705864L, 482705865L, 482705866L, 482747850L, 482748026L, 482748027L, 482748028L, 482748029L, 482748030L, 482846244L, 482846245L, 482846246L, 482846247L, 482846248L, 482846249L, 482890483L, 482890484L, 482890485L, 482890486L, 482890487L, 482890488L, 482890489L, 482890490L, 482890491L, 482890492L, 482928466L, 482928467L, 482928468L, 482928469L, 482928470L, 482928471L, 482944330L, 482944331L, 482944332L, 482944333L, 482944334L, 482944335L, 482964053L, 482964054L, 482964055L, 482964056L, 482964057L, 482964059L, 482985184L, 482985185L, 482985186L, 482985308L, 482985309L, 482985310L, 482994473L, 482994475L, 482994476L, 482994477L, 482994478L, 482994479L, 483021662L, 483021663L, 483021664L, 483021665L, 483021666L, 483021667L, 483032257L, 483032259L, 483032260L, 483032261L, 483032553L, 483032554L, 483048339L, 483048340L, 483048341L, 483048342L, 483048344L, 483048347L, 483066571L, 483066573L, 483066575L, 483066578L, 483066580L, 483066582L, 483091703L, 483091704L, 483091705L, 483091789L, 483091790L, 483091791L, 483119267L, 483119268L, 483119269L, 483119270L, 483119624L, 483119626L, 483130252L, 483130253L, 483130254L, 483130255L, 483130256L, 483130257L, 483151654L, 483151655L, 483151656L, 483151657L, 483151658L, 483151659L, 483204412L, 483204413L, 483209450L, 483209452L, 483209454L, 483209455L, 483259793L, 483259794L, 483259795L, 483259796L, 483259797L, 483259798L, 483284656L, 483284657L, 483284658L, 483284659L, 483284660L, 483284661L, 483314362L, 483314363L, 483314364L, 483314365L, 483319715L, 483319716L, 483332554L, 483332555L, 483332608L, 483332609L, 483332610L, 483332611L, 483378531L, 483378532L, 483378533L, 483378534L, 483378535L, 483378536L, 483400504L, 483400505L, 483400506L, 483400507L, 483400508L, 483400509L, 483470164L, 483470165L, 483470166L, 483470167L, 483470168L, 483470169L, 483561128L, 483561129L, 483561130L, 483561132L, 483561133L, 483561134L, 483616554L, 483616555L, 483616556L, 483616557L, 483616558L, 483616559L, 483651278L, 483651279L, 483651280L, 483651281L, 483651283L, 483651285L, 483677527L, 483677528L, 483677529L, 483677530L, 483677531L, 483677532L, 483707201L, 483707202L, 483707203L, 483707204L, 483707205L, 483707206L, 483749484L, 483749485L, 483749486L, 483749487L, 483749488L, 483749489L, 483786977L, 483786978L, 483786979L, 483786980L, 483786981L, 483786982L, 483843785L, 483843786L, 483862235L, 483862237L, 483895924L, 483895925L, 483895926L, 483895927L, 483895928L, 483895929L};

        for (long chapterId : chapterIds) {
            String path = "http://resali.kzread.cn/cppartner/1x1/11x0/110x1/" + bookId + "/" + chapterId + ".txt";
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream inputStream = conn.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            while (true) {
                String row = br.readLine();

                if (row == null) {
                    break;
                }

                sb.append(row).append("\n");
            }

            String content = sb.toString();

            System.out.println(chapterId + "\t" + content.length());
        }
    }

}