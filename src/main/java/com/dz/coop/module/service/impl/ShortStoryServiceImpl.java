package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookPropertyRpc;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.*;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.glory.common.utils.DateUtil;
import com.dz.glory.common.vo.Ret;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author supf@dianzhong.com
 * @date 2024/06/25 14:55
 */
@Service
public class ShortStoryServiceImpl implements ShortStoryService {

    private static final Logger logger = LoggerFactory.getLogger(ShortStoryServiceImpl.class);

    //@Value("#{'${book.release.typeV3}'.split(',')}")
    private List<Long> typeV3List;

    //@Value("#{'${book.release.type}'.split(',')}")
    private List<Long> typeList;


    @Resource
    private PartnerService partnerService;

    @Resource
    private SyncService syncService;

    @Resource
    private BookService bookService;

    @Reference
    private AdminBookPropertyRpc adminBookPropertyRpc;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Resource
    private JedisClient jedisClient;

    @Override
    public void updateContentByPartner(String bookId, String cpBookId, Long partnerId) {
        String partnerIdStr = jedisClient.getString(new Key("SAR", "partner"));
        if (StringUtils.isEmpty(partnerIdStr)) {
            logger.info("没有配置自动上架cp，跳过检查内容更新任务");
            return;
        }
        String switchUpdate = jedisClient.getString(new Key("SAR", "switch"));
        if (StringUtils.isNotEmpty(switchUpdate)) {
            logger.info("检查内容更新任务未开启");
            return;
        }
        List<Long> partnerIds = Arrays.stream(partnerIdStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (partnerIds.contains(partnerId)) {
            Partner partner = partnerService.getPartnerById(partnerId);
            if (partner != null) {
                try {
                    ClientService instance = ClientFactory.getInstance(partner.getId());
                    if (StringUtils.isBlank(partner.getAliasId())) {
                        partner.setAliasId(partner.getId() + "");
                    }
                    CPBook cpBook = instance.getBookInfo(partner, cpBookId);
                    try {
                        Ret<AdminBookVO> dbBookRet = adminBookRpc.getBookInfo(bookId);
                        if (dbBookRet != null && dbBookRet.getData() != null) {
                            AdminBookVO dbBook = dbBookRet.getData();
                            //上架状态的书，才有必要检查是否要更新内容
                            if (dbBook.getMarketStatus() != null && dbBook.getMarketStatus() == 1) {
                                List<CPVolume> volumeList = instance.getVolumeList(partner, cpBook.getId());
                                if (checkNeedCover(dbBook, volumeList)) {
                                    syncService.cover(dbBook.getBookId());
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("短篇小说内容自动更新异常,cpBookId:{}", cpBook.getBookId(), e);
                    }
                } catch (Exception e) {
                    logger.error("指定cp{},短篇小说内容自动更新异常", partnerId, e);
                }
            }
        }
    }

    @Override
    public DealTypeParam checkNeedAddType(String bookId) {
        DealTypeParam parseTypeVo = new DealTypeParam();
        parseTypeVo.setNeedType(false);
        parseTypeVo.setNeedTypeV3(false);
        List<BookTypeCenterVO> bookTypeV3VOs = adminBookPropertyRpc.getBookTypeV3(bookId).getData();
        List<BookTypeCenterVO> bookTypeVOs = adminBookPropertyRpc.getBookType(bookId).getData();
        if (CollectionUtils.isNotEmpty(bookTypeVOs)) {
            parseTypeVo.setOneTypeId(bookTypeVOs.get(0).getOneTypeId());
            parseTypeVo.setNeedType(bookTypeVOs.stream().noneMatch(bookTypeCenterVO -> typeList.contains(bookTypeCenterVO.getThreeTypeId())));
        }
        if (CollectionUtils.isNotEmpty(bookTypeV3VOs)) {
            parseTypeVo.setOneTypeId(bookTypeV3VOs.get(0).getOneTypeId());
            parseTypeVo.setNeedTypeV3(bookTypeV3VOs.stream().noneMatch(bookTypeCenterVO -> typeV3List.contains(bookTypeCenterVO.getThreeTypeId())));
        }
        return parseTypeVo;
    }

    private boolean checkNeedCover(AdminBookVO dbBook, List<CPVolume> volumeList) {
        String bookId = dbBook.getBookId();
        //检查是否有短篇标签
        List<BookTypeCenterVO> bookTypeV3VOs = adminBookPropertyRpc.getBookTypeV3(bookId).getData();
        boolean hasV3 = false;
        if (CollectionUtils.isNotEmpty(bookTypeV3VOs)) {
            hasV3 = bookTypeV3VOs.stream().anyMatch(bookTypeCenterVO -> typeV3List.contains(bookTypeCenterVO.getThreeTypeId()));
        }
        if (hasV3) {
            //检查CP更新时间
            BookChapterQueryRequest request = new BookChapterQueryRequest();
            request.setBookId(bookId);
            Ret<List<BookChapterVO>> allBookChapter = adminBookRpc.getAllBookChapter(request);
            if (allBookChapter != null && allBookChapter.getData() != null) {
                List<CPChapter> cpChapters = new ArrayList<>();
                volumeList.forEach(cpVolume -> cpChapters.addAll(cpVolume.getChapterList()));
                Map<String, CPChapter> cpChapterMap = cpChapters.stream().collect(Collectors.toMap(CPChapter::getId, cpChapter -> cpChapter));
                List<BookChapterVO> list = allBookChapter.getData();
                return list.stream().anyMatch(bookChapterVO -> {
                    CPChapter cpChapter = cpChapterMap.get(bookChapterVO.getCpChapterId());
                    if (cpChapter != null && bookChapterVO.getUtime() != null && StringUtils.isNotEmpty(cpChapter.getLastUtime())) {
                        try {
                            Date cpUpdate = DateUtil.parse(cpChapter.getLastUtime());
                            return cpUpdate.after(bookChapterVO.getUtime());
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return false;
                });
            }
        }
        return false;
    }
}
