package com.dz.coop.module.service.report;

import com.dz.coop.module.model.report.HourReportModel;
import com.dz.coop.module.model.report.TopUpdateBookModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2019-03-22 21:16
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class BookReportServiceTest {

    @Resource
    private BookReportService bookReportService;

    @Test
    public void testGetHourReportRecord() throws Exception {
        List<HourReportModel> hourReportRecord = bookReportService.getHourReportRecord();
        System.out.println(hourReportRecord);
    }

    @Test
    public void testGetTopUpdateBookRecord() throws Exception {
        List<TopUpdateBookModel> topUpdateBookRecord = bookReportService.getTopUpdateBookRecord(20);
        System.out.println(topUpdateBookRecord);
    }

    @Test
    public void testGetCurrentDayUpdateNum() throws Exception {
        Integer currentDayUpdateNum = bookReportService.getCurrentDayUpdateNum();
        System.out.println(currentDayUpdateNum);
    }

}