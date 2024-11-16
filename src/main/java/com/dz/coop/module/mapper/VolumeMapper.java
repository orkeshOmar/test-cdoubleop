package com.dz.coop.module.mapper;

import com.dz.coop.common.annotation.Check;
import com.dz.coop.module.model.Volume;
import org.apache.ibatis.annotations.Param;

/**
 * @author panqz 2018-10-29 3:53 PM
 */

public interface VolumeMapper {

    Volume getVolume(@Param("bookId") String bookId, @Param("cpVolumeId") String cpVolumeId);

    String getVolumeById(Long id);

    @Check
    void save(Volume volume);

    /**
     * 更新指定书籍指定卷的cp卷id及卷名
     * @param bookId
     * @param id
     * @param cpVolumeId
     * @param name
     */
    void updateVolumeByBookIdAndVolumeId(@Param("bookId") String bookId, @Param("id") Long id, @Param("cpVolumeId") String cpVolumeId, @Param("name") String name);
}
