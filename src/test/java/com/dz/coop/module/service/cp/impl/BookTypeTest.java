package com.dz.coop.module.service.cp.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookPropertyRpc;
import com.dz.content.api.book.vo.BookTypeRelation;
import com.dz.coop.TestBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2021-04-23 17:29
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class BookTypeTest extends TestBase {

    @Reference
    private AdminBookPropertyRpc adminBookPropertyRpc;

    @Test
    public void testBookCenter() {
        BookTypeRelation bookTypeRelation = new BookTypeRelation();

        bookTypeRelation.setBookId("11000100502");
        bookTypeRelation.setOneTypeId(3L);
        bookTypeRelation.setTwoTypeId(86L);
        bookTypeRelation.setThreeTypeId(591L);

        List<BookTypeRelation> bookTypeRelationList = new ArrayList<>();
        bookTypeRelationList.add(bookTypeRelation);

        Integer data = adminBookPropertyRpc.modifyBookType(bookTypeRelationList).getData();
        System.out.println(data);
    }

}
