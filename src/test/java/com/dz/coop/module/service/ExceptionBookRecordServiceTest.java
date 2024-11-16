package com.dz.coop.module.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 异常书籍记录服务层测试
 * @author: songwj
 * @date: 2020-01-18 14:59
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ExceptionBookRecordServiceTest {

    @Resource
    private ExceptionBookRecordService exceptionBookRecordService;

    @Test
    public void testSaveExceptionBookRecord() throws Exception {
        String msg = "### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '4724-11000000034' for key 'idx_wocpch_cpchapterid_bookid'\n" +
                "### The error may involve defaultParameterMap\n" +
                "### The error occurred while setting parameters\n" +
                "### SQL: UPDATE b_owchcp_chapter          SET cp_chapter_id=?,             volume_id=?,             book_id=?,             name=?,             is_free=?,             is_exist=?,             word_num=?,             is_more=?,             utime=now()          where id=?\n" +
                "### Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '4724-11000000034' for key 'idx_wocpch_cpchapterid_bookid'\n" +
                "; SQL []; Duplicate entry '4724-11000000034' for key 'idx_wocpch_cpchapterid_bookid'; nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '4724-11000000034' for key 'idx_wocpch_cpchapterid_bookid'";
        exceptionBookRecordService.saveExceptionBookRecord(msg);
    }

    @Test
    public void testDeleteExceptionBookRecord() throws Exception {
        String bookId = "11000000373";
        exceptionBookRecordService.deleteExceptionBookRecord(bookId);
    }

}