package com.dz.coop.common.util;

import com.dz.tools.TraceKeyHolder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2019-12-03 17:35
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class EscapeUtilTest {

    @Test
    public void testEscape() throws Exception {
        String srcStr = "第1章&nbsp;&nbsp;孤胆潜入&#40;一&#41;<script>xxx</script><br/><p>啊哦</p><b>加粗的</b><br />换行……<p>嗷嗷啊……</p>";
        System.out.println(srcStr);
        String escape = EscapeUtil.escape(srcStr);
        System.out.println(escape);
    }

    @Test
    public void test2() {
        String[] bookIds = {"11010120133","11010120183","11010120184","11010120185","11010120186","11010120187","11010120188","11010120189","11010120190","11010120191","11010120192","11010120193","11010120194","11010120195","11010120196","11010120197","11010120198","11010120199","11010120200","11010120201","11010120202","11010120203","11010120204","11010120205"};
        for (String bookId : bookIds) {
            HttpUtil.sendGet("http://api.wwread.cn/coop-client/portal/book/updateBookInfo/" + bookId + "?needUpdateCover=false&needUpdateBookName=false&needUpdateIntroduction=true&marketStatus=");
            System.out.println("书籍" + bookId + "信息更新成功！");
        }
    }

    @Test
    public void test3() {
        String[] bookIds = {"11000107544","11000109922","11000110957","11000112963","11000114421","11000116203","11000117082","11000117084","11000117085","11000118120","11000119325","11000119355","11000119356","11000120304","11000123156","11000123157","11000123580","11000124701","11000124956","11000144602","11000145341","11000145344","11000145345","11000145346","11000145348","11000145350","11000145352","11000145353","11000145355","11000147499"};
        for (String bookId : bookIds) {
            HttpUtil.sendGet("http://101.251.204.195:9095/msg/book/push?bookId=" + bookId + "&type=1");
            System.out.println("书籍" + bookId + "封面更新成功！");
        }
    }

    @Test
    public void test4() throws Exception {
        FileWriter out = new FileWriter("D:/11010002188.txt");
        File file = new File("D:/11010002188/");
        File[] files = file.listFiles();

        for (File f : files) {
            System.out.println(f.getAbsolutePath());
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f.getAbsolutePath()), "utf-8"));

            while (true) {
                String row = br.readLine();

                if (row == null) {
                    break;
                }

                out.write(row + "\r\n");
            }

            br.close();
        }

        out.close();
    }

    @Test
    public void test5() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/20200313huaweiCompare.txt"), "utf-8"));
        int num = 1;

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String[] split = row.split("\\t");
            int overseaNum = Integer.parseInt(split[1]);
            int masterNum = Integer.parseInt(split[4]);

            if (masterNum < overseaNum) {
                System.out.println(split[0] + "-[" + num + "]overseaNum=" + overseaNum + ", masterNum=" + masterNum);
                num++;
            }
        }
    }

    @Test
    public void test6() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/urls.txt"), "utf-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/results.txt")));

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String[] split = row.split("\\t");
            int cpId = Integer.parseInt(split[0]);
            String url = split[1];

            try {
                HttpUtil.sendGet(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String ret = TraceKeyHolder.getUserKey("ret");
            ret = StringUtils.isNotBlank(ret) ? (ret.length() > 150 ? ret.substring(0, 150) : ret) : ret;
            System.out.println(cpId + "\t" + ret);
            writer.write(cpId + "\t" + ret + "\n");
        }

        br.close();
        writer.close();
    }

    @Test
    public void test7() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/md5src3.txt"), "utf-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/md5_3.txt")));

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String md5Hex = DigestUtils.md5Hex(row.trim());
            System.out.println(row + "：" + md5Hex);
            writer.write(md5Hex + "\n");
        }

        br.close();
        writer.close();
    }

    @Test
    public void test8() {
        String[] bookIds = {"11010076367","11010076410","11010076373","11010076342","11010076388","11010076425","11010076316","11010076384","11010076426","11010076270","11010076374","11010076267","11010076372","11010076405","11010076352","11010076449","11010076393","11010076340","11010076409","11010076332","11010076344","11010076370","11010076327","11010076364","11010076221","11010076200","11010076240","11010076378","11010076323","11010056202","11010076429","11010076265","11010076358","11010076445","11010076376","11010076391","11010076322","11010076359","11010076403","11010076361","11010076394","11010076356","11010076330","11010076261","11010076198","11010076284","11010076182","11010076289","11010076236","11010076395","11010076427","11010071259","11010076354","11010076266","11010076366","11010076274","11010076389","11010076193","11010076276","11010076242","11010076331","11010076238","11010076317","11010076287","11010076324","11010076347","11010076349","11010076209","11010076260"};
        for (String bookId : bookIds) {
            HttpUtil.sendGet("http://api.wwread.cn/coop-client/portal/book/book/" + bookId);
            System.out.println("书籍" + bookId + "章节抓取成功！");
        }
    }

    @Test
    public void test9() {
        String content = "https://cpaudio.oss-cn-beijing.aliyuncs.com/108010/5732485.mp3";
        int i = content.indexOf("/", 9);
        System.out.println(content.substring(i));
    }

    @Test
    public void test10() throws Exception {
        System.out.println("对比开始");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/asg_b_book.txt"), "utf-8"));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("D:/glory_b_book.txt"), "utf-8"));
        Map<String, String> map = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();

        while (true) {
            String row = br.readLine();

            if (row == null) {
                break;
            }

            String[] split = row.split("\t");
            map.put(split[0], split[1]);
        }

        while (true) {
            String row = br2.readLine();

            if (row == null) {
                break;
            }

            String[] split = row.split("\t");
            map2.put(split[0], split[1]);
        }

        System.out.println(map.size() + " -> " + map2.size());

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> me : entries) {
            String bookId = me.getKey();
            String marketStatus = me.getValue();
            if (StringUtils.equals(marketStatus, "8")) {
                marketStatus = "3";
            } else if (StringUtils.equals(marketStatus, "10")) {
                marketStatus = "4";
            }

            if (map2.containsKey(bookId)) {
                String ms = map2.get(bookId);

                if (!StringUtils.equals(marketStatus, ms)) {
                    System.out.println(bookId + " " + me.getValue() + " " + ms);
                }
            }
        }

        System.out.println("对比结束");

        br.close();
        br2.close();
    }

}