package com.dz.coop.module.controller;

import com.alibaba.fastjson.JSONObject;
import com.dz.coop.module.constant.ICacheRedisKey;
import com.dz.coop.module.model.AudioTrailer;
import com.dz.coop.module.service.AudioTrailerService;
import com.dz.jedis.client.JedisClient;
import com.dz.tools.JsonUtil;
import com.dz.vo.Ret;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @project: coop-client
 * @description: CPS片花控制器
 * @author: songwj
 * @date: 2020-12-02 20:54
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RestController
@RequestMapping("/portal/cps/trailers")
public class TrailersController {

    private Logger logger = LoggerFactory.getLogger(TrailersController.class);

    @Resource
    private AudioTrailerService audioTrailerService;

    @Resource
    private JedisClient newJedisClient;

    /**
     * 查询指定书籍列表的片花数据
     * @param body
     * @return
     */
    @PostMapping("/getTrailersByBookId")
    public Ret getTrailersByBookId(@RequestBody String body) {
        try {
            if (StringUtils.isBlank(body)) {
                return Ret.error(1, "请求体不能为空");
            }

            JSONObject jsonObject = JsonUtil.readValue(body, JSONObject.class);
            body = jsonObject.toString();
            logger.info("CPS获取片花请求体: {}", body);

            String bookIds = jsonObject.getString("bookId");

            if (StringUtils.isBlank(bookIds)) {
                return Ret.error(2, "查询的书籍ID不能为空");
            }

            String[] bookIdArr = bookIds.split("-");
            List<AudioTrailer> audioTrailerList = new ArrayList<>();
            Set<String> needSearchBookIds = new HashSet<>();

            for (String bookId : bookIdArr) {
                List<AudioTrailer> trailers = newJedisClient.get(ICacheRedisKey.DB_2_TRAILER, String.format(ICacheRedisKey.TRAILER_KEY, bookId), new TypeReference<List<AudioTrailer>>() {});

                if (trailers == null) {
                    needSearchBookIds.add(bookId);
                } else {
                    audioTrailerList.addAll(trailers);
                }
            }

            if (CollectionUtils.isNotEmpty(needSearchBookIds)) {
                List<AudioTrailer> audioTrailers = audioTrailerService.getAudioTrailerByBooIds(StringUtils.join(needSearchBookIds, ","));
                Map<String, List<AudioTrailer>> tempMap = new HashMap<>(audioTrailers.size());

                if (CollectionUtils.isNotEmpty(audioTrailers)) {
                    for (AudioTrailer audioTrailer : audioTrailers) {
                        String bookId = audioTrailer.getBookId();

                        if (tempMap.containsKey(bookId)) {
                            tempMap.get(bookId).add(audioTrailer);
                        } else {
                            List<AudioTrailer> trailerList = new ArrayList<>();
                            trailerList.add(audioTrailer);
                            tempMap.put(bookId, trailerList);
                        }
                    }
                }

                for (String bookId : needSearchBookIds) {
                    if (tempMap.containsKey(bookId)) {
                        List<AudioTrailer> trailers = tempMap.get(bookId);
                        audioTrailerList.addAll(trailers);
                        newJedisClient.set(ICacheRedisKey.DB_2_TRAILER, String.format(ICacheRedisKey.TRAILER_KEY, bookId), trailers, 24 * 60 * 60);
                    } else {
                        newJedisClient.set(ICacheRedisKey.DB_2_TRAILER, String.format(ICacheRedisKey.TRAILER_KEY, bookId), Collections.EMPTY_LIST, 24 * 60 * 60);
                    }
                }
            }

            logger.info("CPS查询片花返回数据量：{}", audioTrailerList.size());
            return Ret.success(audioTrailerList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "查询失败");
        }
    }

}
