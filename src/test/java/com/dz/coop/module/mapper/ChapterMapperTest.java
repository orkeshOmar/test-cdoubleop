package com.dz.coop.module.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.module.model.ChapterExtend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 章节处理测试
 * @author: songwj
 * @date: 2019-07-29 13:50
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ChapterMapperTest {

    @Resource
    private ChapterMapper chapterMapper;

    @Test
    public void testSaveChapterExtend() throws Exception {
        ChapterExtend extend = new ChapterExtend();
        extend.setChapterId("123");
        Map<String, Object> map = new HashMap<>();
        map.put("suffix", "m4a");
        map.put("size", "1024");
        extend.setExtend(JSON.toJSONString(map));
        chapterMapper.saveChapterExtend(extend);
    }

    @Test
    public void testGetChapterExtendByChapterId() throws Exception {
        ChapterExtend chapterExtend = chapterMapper.getChapterExtendByChapterId("123");
        System.out.println(chapterExtend);
    }

    @Test
    public void testUpdateChapterExtend() throws Exception {
        ChapterExtend chapterExtend = chapterMapper.getChapterExtendByChapterId("123");
        String extend = chapterExtend.getExtend();
        JSONObject extendObj = JSONObject.parseObject(extend);
        Map<String, Object> map = extendObj;
        map.put("suffix", "mp3");
        chapterExtend.setExtend(JSON.toJSONString(map));
        chapterMapper.updateChapterExtend(chapterExtend);
    }

}