package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookPropertyRpc;
import com.dz.content.api.book.rpc.AdminBookTypeRpc;
import com.dz.content.api.book.rpc.AdminBookTypeV3Rpc;
import com.dz.content.api.book.vo.*;
import com.dz.coop.module.mapper.*;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.PartnerCategoryModel;
import com.dz.coop.module.service.BookCategoryService;
import com.dz.glory.common.vo.Ret;
import com.dz.tools.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @project: coop-client
 * @description: 书籍分类服务实现
 * @author: songwj
 * @date: 2019-05-17 16:29
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BookCategoryServiceImpl implements BookCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(BookCategoryServiceImpl.class);

    @Resource
    private BookTypeCenterMapper bookTypeCenterMapper;

    @Resource
    private BookPartnerCategoryMapper bookPartnerCategoryMapper;

    @Resource
    private BookTypeMapper bookTypeMapper;

    @Resource
    private BookTypeTwoMapper bookTypeTwoMapper;

    @Resource
    private BookTypeThreeMapper bookTypeThreeMapper;

    @Reference
    private AdminBookTypeRpc adminBookTypeRpc;

    @Reference
    private AdminBookTypeV3Rpc adminBookTypeV3Rpc;

    @Reference
    private AdminBookPropertyRpc adminBookPropertyRpc;

    @Override
    public boolean addCategory(String bookId, Long cpId, String cpCategoryId) {
        try {

            //处理新版本分类，不影响老分类
            addCategoryV3(bookId, cpId, cpCategoryId);

            List<BookTypeCenterVO> bookTypeCenterVOs = adminBookPropertyRpc.getBookType(bookId).getData();

            if (CollectionUtils.isNotEmpty(bookTypeCenterVOs)) {
                logger.info("[cpId={}][bookId={}]书籍分类已经存在，不再自动匹配分类", cpId.toString(), bookId);
                return false;
            }

            PartnerCategoryModel category = bookPartnerCategoryMapper.getCategory(cpId, cpCategoryId);

            if (Objects.isNull(category)) {
                logger.info("[cpId={}][bookId={}][cpCategoryId={}]没有查询到书籍的新分类映射信息，不自动匹配新分类", cpId.toString(), bookId, cpCategoryId);
                bookTypeMapper.save(bookId, Long.parseLong(Book.DEFAULT_BOOK_TYPE));
                return false;
            }

            // 分类等级
            Integer typeLevel = category.getTypeLevel();
            List<Long> typeIds = Arrays.stream(category.getTypeIdSet()
                            .split(","))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::valueOf)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            // 不存在新字段则使用可能的老字段
            if (CollectionUtils.isEmpty(typeIds) && Objects.nonNull(category.getTypeId())) {
                typeIds = new ArrayList<Long>(){{add(Long.valueOf(category.getTypeId()));}};
            }

            if (typeLevel != null && typeLevel == 2 && CollectionUtils.isNotEmpty(typeIds)) {
                List<BookTypeTwoVO> bookTypeTwoVOList = typeIds.stream()
                        .map(adminBookTypeRpc::getBookTypeTwoById)
                        .filter(Objects::nonNull)
                        .map(Ret::getData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(bookTypeTwoVOList)) {
                    List<BookTypeRelation> bookTypeRelationList = bookTypeTwoVOList.stream()
                            .map(bookTypeTwoVO -> {
                                BookTypeRelation bookTypeRelation = new BookTypeRelation();
                                bookTypeRelation.setBookId(bookId);
                                bookTypeRelation.setOneTypeId(bookTypeTwoVO.getOneTypeId());
                                bookTypeRelation.setTwoTypeId(bookTypeTwoVO.getId());
                                return bookTypeRelation;
                            })
                            .collect(Collectors.toList());

                    adminBookPropertyRpc.modifyBookType(bookTypeRelationList);
                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]匹配到本地二级分类信息：{}", cpId.toString(), bookId, cpCategoryId, JsonUtil.obj2Json(bookTypeRelationList));
                } else {
                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]本地未查询到[typeId={}]所对应的二级分类信息", cpId.toString(), bookId, cpCategoryId, typeIds);
                }
            } else if (typeLevel != null && typeLevel == 3 && CollectionUtils.isNotEmpty(typeIds)) {
                List<BookTypeThreeVO> bookTypeThreeVOList = typeIds.stream()
                        .map(adminBookTypeRpc::getBookTypeThreeById)
                        .filter(Objects::nonNull)
                        .map(Ret::getData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(bookTypeThreeVOList)) {
                    List<BookTypeRelation> bookTypeRelationList = bookTypeThreeVOList.stream()
                            .map(bookTypeThreeVO -> {
                                BookTypeRelation bookTypeRelation = new BookTypeRelation();
                                bookTypeRelation.setBookId(bookId);
                                bookTypeRelation.setOneTypeId(bookTypeThreeVO.getOneTypeId());
                                bookTypeRelation.setTwoTypeId(bookTypeThreeVO.getTwoTypeId());
                                bookTypeRelation.setThreeTypeId(bookTypeThreeVO.getId());
                                return bookTypeRelation;
                            })
                            .collect(Collectors.toList());

                    adminBookPropertyRpc.modifyBookType(bookTypeRelationList).getData();

                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]匹配到本地三级分类信息：{}", cpId.toString(), bookId, cpCategoryId, JsonUtil.obj2Json(bookTypeRelationList));
                } else {
                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]本地未查询到[typeId={}]所对应的三级分类信息", cpId.toString(), bookId, cpCategoryId, typeIds);
                }
            } else {
                logger.error("[cpId={}][bookId={}][cpCategoryId={}]CP新分类等级有误，只能为2或3");
            }

            // 旧版分类默认对应期刊
            bookTypeMapper.save(bookId, Long.parseLong(Book.DEFAULT_BOOK_TYPE));
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public PartnerCategoryModel getCategory(Long cpId, String cpCategoryId) {
        return bookPartnerCategoryMapper.getCategory(cpId, cpCategoryId);
    }

    private void addCategoryV3(String bookId, Long cpId, String cpCategoryId) {
        try {

            List<BookTypeCenterVO> bookTypeCenterVOs = adminBookPropertyRpc.getBookTypeV3(bookId).getData();

            if (CollectionUtils.isNotEmpty(bookTypeCenterVOs)) {
                logger.info("[cpId={}][bookId={}]书籍分类已经存在，不再自动匹配分类", cpId.toString(), bookId);
                return;
            }

            PartnerCategoryModel category = bookPartnerCategoryMapper.getCategory(cpId, cpCategoryId);

            if (Objects.isNull(category)) {
                logger.info("[cpId={}][bookId={}][cpCategoryId={}]没有查询到书籍的新分类映射信息，不自动匹配新分类", cpId.toString(), bookId, cpCategoryId);
                bookTypeMapper.save(bookId, Long.parseLong(Book.DEFAULT_BOOK_TYPE));
                return;
            }

            // 分类等级
            Integer typeV3Level = category.getTypeV3Level();
            List<Long> typeV3Ids = Arrays.stream(category.getTypeV3IdSet()
                            .split(","))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::valueOf)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            // 不存在新字段则使用可能的老字段
            if (CollectionUtils.isEmpty(typeV3Ids) && Objects.nonNull(category.getTypeV3Id())) {
                typeV3Ids = new ArrayList<Long>(){{add(Long.valueOf(category.getTypeV3Id()));}};
            }

            if (typeV3Level != null && typeV3Level == 3 && CollectionUtils.isNotEmpty(typeV3Ids)) {
                List<Long> bookTypeThreeVOIds = typeV3Ids.stream()
                        .map(adminBookTypeV3Rpc::getBookTypeThreeById)
                        .filter(Objects::nonNull)
                        .map(Ret::getData)
                        .filter(Objects::nonNull)
                        .map(BookTypeThreeV3VO::getId)
                        .collect(Collectors.toList());


                if (CollectionUtils.isNotEmpty(bookTypeThreeVOIds)) {

                    BookTypeRelationV3 bookTypeRelationV3 = new BookTypeRelationV3();

                    bookTypeRelationV3.setBookId(bookId);
                    bookTypeRelationV3.setThreeTypeIds(bookTypeThreeVOIds);

                    Integer data = adminBookPropertyRpc.modifyBookTypeV3(bookTypeRelationV3).getData();

                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]匹配到本地新版三级分类信息：{}", cpId.toString(), bookId, cpCategoryId, JsonUtil.obj2Json(bookTypeRelationV3));
                } else {
                    logger.info("[cpId={}][bookId={}][cpCategoryId={}]本地未查询到[typeId={}]所对应的新版三级分类信息", cpId.toString(), bookId, cpCategoryId, typeV3Ids);
                }
            } else {
                logger.error("[cpId={}][bookId={}][cpCategoryId={}]CP新分类等级有误，只能为3", cpId.toString(), bookId, cpCategoryId);
            }
        } catch (Exception e) {
            logger.error("新版分类匹配失败", e);
        }
    }

}
