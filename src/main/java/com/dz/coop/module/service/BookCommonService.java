package com.dz.coop.module.service;

import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;

import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍公共接口服务，用于查询书籍列表、最大章节，章节内容等
 * @author: songwj
 * @date: 2019-05-13 17:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public interface BookCommonService {

    List<CPBook> getBookList(Partner owchPartner) throws Exception;

    CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception;

    List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception;

    CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String chapterId) throws Exception;

    CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception;

    CPChapter getMaxCpChapterInfo(Partner owchPartner, String bookId) throws Exception;

}
