package com.dz.coop.module.mapper;

import com.dz.coop.module.model.AudioTrailer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @project: coop-client
 * @description: 音频片花 Mapper
 * @author: songwj
 * @date: 2020-12-02 22:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface AudioTrailerMapper {

    /**
     * 查询指定书籍ID的片花
     * @param bookIds
     * @return
     */
    List<AudioTrailer> getAudioTrailerByBooIds(@Param("bookIds") String bookIds);

    /**
     * 插入书籍片花
     * @param audioTrailer
     */
    void insertAudioTrailer(AudioTrailer audioTrailer);

}
