package com.dz.coop.module.service.impl;

import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.BookCommonService;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍公共接口服务，用于查询书籍列表、最大章节，章节内容等
 * @author: songwj
 * @date: 2019-05-13 17:58
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BookCommonServiceImpl implements BookCommonService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        return instance.getBookList(owchPartner);
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        return instance.getBookInfo(owchPartner, bookId);
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        return instance.getVolumeList(owchPartner, bookId);
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        return instance.getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        return instance.getCPChapterInfo(owchPartner, cpBookId, volumeId, chapterId);
    }

    @Override
    public CPChapter getMaxCpChapterInfo(Partner owchPartner, String bookId) throws Exception {
        ClientService instance = ClientFactory.getInstance(owchPartner.getId());

        if (StringUtils.isBlank(owchPartner.getAliasId())) {
            owchPartner.setAliasId(owchPartner.getId() + "");
        }

        List<CPVolume> volumeList = instance.getVolumeList(owchPartner, bookId);

        if (CollectionUtils.isNotEmpty(volumeList)) {
            List<CPChapter> chapterList = volumeList.get(volumeList.size() - 1).getChapterList();

            if (CollectionUtils.isNotEmpty(chapterList)) {
                return chapterList.get(chapterList.size() - 1);
            }
        }

        return null;
    }

}
