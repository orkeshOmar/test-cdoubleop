package com.dz.coop.module.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @project: coop-client
 * @description: 章节目录获取工具类
 * @author: songwj
 * @date: 2019-07-26 20:24
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ChapterSupportTest {

    @Test
    public void testGetChapterPath() throws Exception {
        String chapterPath = ChapterSupport.getChapterPath("11000000027", 100158L);
        System.out.println(chapterPath);
    }

    @Test
    public void testGetAudioChapterPath() throws Exception {
        String chapterPath = ChapterSupport.getAudioChapterPath("31000000027", 100158L, ChapterSupport.AUDIO_CHAPTER_SUFFIX);
        System.out.println(chapterPath);
    }
}