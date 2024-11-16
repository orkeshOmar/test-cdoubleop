package com.dz.coop.module.service.impl;

import com.dz.coop.module.constant.ICacheRedisKey;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.mapper.AudioTrailerMapper;
import com.dz.coop.module.model.AudioTrailer;
import com.dz.coop.module.service.AudioTrailerService;
import com.dz.jedis.client.JedisClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 音频片花服务层
 * @author: songwj
 * @date: 2020-12-02 23:20
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class AudioTrailerServiceImpl implements AudioTrailerService {

    private Logger logger = LoggerFactory.getLogger(AudioTrailerServiceImpl.class);

    @Resource
    private AudioTrailerMapper audioTrailerMapper;

    @Resource
    private JedisClient newJedisClient;

    @Override
    public List<AudioTrailer> getAudioTrailerByBooIds(String bookIds) {
        return audioTrailerMapper.getAudioTrailerByBooIds(bookIds);
}

    @Override
    public void insertAudioTrailer(AudioTrailer audioTrailer) {
        audioTrailerMapper.insertAudioTrailer(audioTrailer);
        logger.info("bookId={},trailerId={},url={} 片花保存成功", audioTrailer.getBookId(), audioTrailer.getTrailerId(), audioTrailer.getUrl());
    }

    @Override
    public void saveTrailers(Long cpId, List<AudioTrailer> audioTrailers, String bookId) {
        try {
            if (ThirdPart.ZHONG_QI_RUI_MING.getCpId().equals(cpId) && CollectionUtils.isNotEmpty(audioTrailers) && StringUtils.isNotBlank(bookId)) {
                List<AudioTrailer> trailers = getAudioTrailerByBooIds(bookId);

                if (CollectionUtils.isNotEmpty(trailers)) {
                    Map<Long, Long> map = new HashMap<>(8);

                    for (AudioTrailer audioTrailer : trailers) {
                        map.put(audioTrailer.getTrailerId(), audioTrailer.getLastUtime().getTime());
                    }

                    Iterator<AudioTrailer> iterator = audioTrailers.iterator();

                    while (iterator.hasNext()) {
                        AudioTrailer next = iterator.next();
                        Long trailerId = next.getTrailerId();

                        if (map.containsKey(trailerId) && next.getLastUtime().getTime() <= map.get(trailerId)) {
                            iterator.remove();
                        }
                    }
                }

                audioTrailers.forEach(audioTrailer -> {
                    audioTrailer.setBookId(bookId);
                    insertAudioTrailer(audioTrailer);
                });

                // 更新书籍片花缓存
                if (CollectionUtils.isNotEmpty(audioTrailers)) {
                    List<AudioTrailer> trailerList = getAudioTrailerByBooIds(bookId);
                    newJedisClient.set(ICacheRedisKey.DB_2_TRAILER, String.format(ICacheRedisKey.TRAILER_KEY, bookId), trailerList, 24 * 60 * 60);
                    logger.info("bookId={} 片花缓存更新成功", bookId);
                }
            }
        } catch (Exception e) {
            logger.error("书籍bookId={}片花保存失败：{}", e.getMessage(), e);
        }
    }

}
