package com.dz.coop.common.util;

import org.junit.Test;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2020-11-24 20:53
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class StringUtilTest {

    @Test
    public void testRemoveEmptyLines() throws Exception {
        String str = "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "　　\n" +
                "\n" +
                "　　\n" +
                "\n" +
                "　　\n" +
                "\n" +
                "　　\n";
        String s = StringUtil.removeEmptyLines(str);
        System.out.println(s);
    }

    @Test
    public void testContainsHtml() {
        String str = "<html><h1>xxx</h1></html>\n&lt;html&gt;&lt;h1&gt;xxx&lt;/h1&gt;&lt;/html&gt;";
        System.out.println(StringUtil.containsHtml(str));
        str = "阿达阿达打<html><h1>xxx</h1></html>\n&lt;html&gt;&lt;h1&gt;xxx&lt;/h1&gt;&lt;/html&gt;发飞飞飞是是的是的";
        System.out.println(StringUtil.containsHtml(str));
        str = "<p style=\"text-align: left;\" class=\"MsoNormal\">\n第一件事情，骨龙飞的并不高，实际上这种亡灵生物翅膀上的肉，早已经掉光了，现在帮助牠飞翔的，只不过是一种特殊的，在死亡之后通过灵活魔法，吸引灵魂物质生成的类似粘膜的东西，虽然可以让牠飞起来，但是也只能达到普通飞鸟的高度，虽然已经不低了，但是对于体积庞大的牠来说，轻易的可以让人在地面上看得一清二楚。";
        System.out.println(StringUtil.containsHtml(str));
        str = "“既然如此的话，今天你们一个也跑不了，当年我男人为了这个<span style=\"background-color: rgb(206, 218, 194);\">臭女人</span>，竟然把家产都败光了，但这个<span style=\"background-color: rgb(206, 218, 194);\">臭女人</span>";
        System.out.println(StringUtil.containsHtml(str));
        str = "第一件事情，骨龙飞的并不高，实际上这种亡灵生物翅膀上的肉，早已经掉光了，现在帮助牠飞翔的，只不过是一种特殊的，在死亡之后通过灵活魔法，吸引灵魂物质生成的类似粘膜的东西，虽然可以让牠飞起来，但是也只能达到普通飞鸟的高度，虽然已经不低了，但是对于体积庞大的牠来说，轻易的可以让人在地面上看得一清二楚。";
        System.out.println(StringUtil.containsHtml(str));
        str = "第一件事情，骨龙飞的并不高，实际上这种亡灵生物翅膀上的肉，《钢铁是怎样练成的》早已经掉光了，现在帮助牠飞翔的，只不过是一种特殊的，在死亡之后通过灵活魔法，吸引灵魂物质生成的类似粘膜的东西，虽然可以让牠飞起来，但是也只能达到普通飞鸟的高度，虽然已经不低了，但是对于体积庞大的牠来说，轻易的可以让人在地面上看得一清二楚。";
        System.out.println(StringUtil.containsHtml(str));
        str = "１９８２年，李树喜调入<光明日报>机动部任记者不久，一位特殊的读者找到了他，这个人就是当年“阿波丸”打捞工程的现场指挥张智魁。";
        System.out.println(StringUtil.containsHtml(str));
        str = "１９８２年，李树喜调入<H9527>机动部任记者不久，一位特殊的读者找到了他，<光明日报>这个人就是当年“阿波丸”打捞工程的现场指挥张智魁。";
        System.out.println(StringUtil.containsHtml(str));
        str = "１９８２年，李树喜调入<光H9527>机动部任记者不久，一位特殊的读者找到了他，这个人就是当年“阿波丸”打捞工程的现场指挥张智魁。";
        System.out.println(StringUtil.containsHtml(str));
    }

}