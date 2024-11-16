package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.BookVolumeRequest;
import com.dz.content.api.book.vo.BookVolumeVO;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.BeanUtil;
import com.dz.coop.common.util.CheckUtil;
import com.dz.coop.common.util.EscapeUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.mapper.VolumeMapper;
import com.dz.coop.module.model.Volume;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.VolumeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author panqz 2018-12-11 11:36 PM
 */
@Service
public class VolumeServiceImpl implements VolumeService {

    private static final Logger logger = LoggerFactory.getLogger(VolumeServiceImpl.class);

    @Resource
    private VolumeMapper volumeMapper;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Override
    public Long saveVolums(CPVolume volume, String bookId) {

        try {
            //Volume owchVolume = volumeMapper.getVolume(bookId, volume.getId());
            BookVolumeRequest request = new BookVolumeRequest();
            request.setBookId(bookId);
            request.setCpVolumeId(volume.getId());
            BookVolumeVO bookVolumeVO = adminBookRpc.queryByBookIdAndCpVolumeId(request).getData();

            if (bookVolumeVO == null) {
                Volume owchVolume = new Volume();

                owchVolume.setBookId(bookId);
                owchVolume.setName(StringUtils.defaultIfBlank(volume.getName(), "正文"));
                owchVolume.setCpVolumeId(volume.getId());

                CheckUtil.check(owchVolume);
                EscapeUtil.escape(owchVolume);

                bookVolumeVO = BeanUtil.convertFrom(owchVolume, BookVolumeVO.class);

                //volumeMapper.save(owchVolume);
                bookVolumeVO = adminBookRpc.addBookVolume(bookVolumeVO).getData();

                if (bookVolumeVO == null) {
                    throw new BookException("volumeId={},volumeName={} 卷插入数据库异常", volume.getId(), volume.getName());
                }

                logger.info("volumeId={},volumeName={} 卷插入数据库成功", volume.getId(), volume.getName());
            }

            return bookVolumeVO.getId();

        } catch (Exception e) {
            throw new BookException("volumeId={},volumeName={} 卷插入数据库异常", volume.getId(), volume.getName());
        }

    }
}
