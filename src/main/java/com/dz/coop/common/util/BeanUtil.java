package com.dz.coop.common.util;

import com.dz.content.api.book.vo.AdminBookVO;
import com.dz.content.api.book.vo.AdminCpPartnerVO;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerUrl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;

/**
 * @project: coop-client
 * @description: Bean处理工具类
 * @author: songwj
 * @date: 2021-04-15 16:23
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class BeanUtil {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 将List转为指定对象List
     * @param source
     * @param tClass
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E, T> List<T> convertList2List(List<E> source, Class<T> tClass) {
        List<T> output = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(source)) {
            source.forEach(src -> {
                T target = BeanUtils.instantiate(tClass);
                BeanUtils.copyProperties(src, target);
                output.add(target);
            });
        }

        return output;
    }

    public static <F, T> T convertFrom(F from, Class<T> clazz) {
        try {
            if (Objects.nonNull(from)) {
                T copy = clazz.newInstance();
                BeanUtils.copyProperties(from, copy);
                return copy;
            }
        } catch (InstantiationException e) {
            logger.error("类型转换失败:{}", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("类型转换失败:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 转换书籍对象
     * @param from
     * @return
     */
    public static Book convert2Book(AdminBookVO from) {
        if (Objects.nonNull(from)) {
            Book book = new Book();
            BeanUtils.copyProperties(from, book);
            book.setPartnerId(from.getCpPartnerId().toString());
            return book;
        }

        return null;
    }

    /**
     * 转换Partner对象
     * @param from
     * @return
     */
    public static Partner convert2Partner(AdminCpPartnerVO from) {
        if (Objects.nonNull(from)) {
            Partner partner = new Partner();
            BeanUtils.copyProperties(from, partner);
            partner.setApiKey(from.getApikey());
            partner.setBookType(Integer.parseInt(from.getBookType()));

            PartnerUrl partnerUrl = new PartnerUrl();
            partnerUrl.setPid(from.getId());
            partnerUrl.setBookListUrl(from.getBooklistUrl());
            partnerUrl.setBookInfoUrl(from.getBookinfoUrl());
            partnerUrl.setChapterListUrl(from.getChapterlistUrl());
            partnerUrl.setChapterInfoUrl(from.getChapterinfoUrl());
            partnerUrl.setCategoryListUrl(from.getCatelistUrl());

            partner.setUrl(partnerUrl);

            return partner;
        }

        return null;
    }

    /**
     * 转换为Partner列表
     * @param from
     * @return
     */
    public static List<Partner> convert2PartnerList(List<AdminCpPartnerVO> from) {
        List<Partner> output = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(from)) {
            from.forEach(src -> {
                Partner partner = new Partner();
                BeanUtils.copyProperties(src, partner);

                partner.setApiKey(src.getApikey());
                partner.setBookType(Integer.parseInt(src.getBookType()));

                PartnerUrl partnerUrl = new PartnerUrl();
                partnerUrl.setPid(src.getId());
                partnerUrl.setBookListUrl(src.getBooklistUrl());
                partnerUrl.setBookInfoUrl(src.getBookinfoUrl());
                partnerUrl.setChapterListUrl(src.getChapterlistUrl());
                partnerUrl.setChapterInfoUrl(src.getChapterinfoUrl());

                partner.setUrl(partnerUrl);

                output.add(partner);
            });
        }

        return output;
    }

}
