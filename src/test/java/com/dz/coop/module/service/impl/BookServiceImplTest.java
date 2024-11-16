package com.dz.coop.module.service.impl;

import com.dz.coop.TestBase;
import com.dz.coop.module.service.BookService;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2021-04-25 15:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class BookServiceImplTest extends TestBase {

    @Resource
    private BookService bookService;

    @Test
    public void testListRecentUpdateBooks() throws Exception {
        List<String> bookIds = bookService.listRecentUpdateBooks();
        System.out.println(bookIds.size());
    }

    @Test
    public void testGetAllBreakSourceBookIds() throws Exception {
        List<String> bookIds = bookService.getAllBreakSourceBookIds();
        System.out.println(bookIds.size());
    }

    @Test
    public void testGetAllNeedUpdateAudioBookId() throws Exception {
        List<String> bookIds = bookService.getAllNeedUpdateAudioBookId();
        System.out.println(bookIds);
    }

}