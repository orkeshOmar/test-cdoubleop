package com.dz.coop.module.service;

import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.coop.module.model.AudioTrailer;

import java.util.List;

/**
 * @project: coop-client
 * @description: 音频片花服务层
 * @author: songwj
 * @date: 2020-12-02 23:18
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface AudioTrailerService {

    void insertAudioTrailer(AudioTrailer audioTrailers);

    @DynamicDataSource("dataSourceReadOnly")
    List<AudioTrailer> getAudioTrailerByBooIds(String bookIds);

    /**
     * 保存片花
     * @param audioTrailers
     * @param bookId
     */
    void saveTrailers(Long cpId, List<AudioTrailer> audioTrailers, String bookId);

}
