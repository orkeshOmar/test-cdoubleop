package com.dz.coop.module.service.report.impl;

import com.dz.coop.module.mapper.BookReportMapper;
import com.dz.coop.module.model.report.HourReportModel;
import com.dz.coop.module.model.report.NewBookModel;
import com.dz.coop.module.model.report.TopUpdateBookModel;
import com.dz.coop.module.service.report.BookReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍报表服务实现
 * @author: songwj
 * @date: 2019-03-22 20:23
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class BookReportServiceImpl implements BookReportService {

    @Resource
    private BookReportMapper bookReportMapper;

    @Override
    public List<HourReportModel> getHourReportRecord() {
        return bookReportMapper.getHourReportRecord();
    }

    @Override
    public List<TopUpdateBookModel> getTopUpdateBookRecord(Integer topNum) {
        return bookReportMapper.getTopUpdateBookRecord(topNum);
    }

    @Override
    public Integer getCurrentDayUpdateNum() {
        return bookReportMapper.getCurrentDayUpdateNum();
    }

    @Override
    public Integer getCurrentDayAddBookNum() {
        return bookReportMapper.getCurrentDayAddBookNum();
    }

    @Override
    public List<NewBookModel> getCurrentDayAddBookRecord() {
        return bookReportMapper.getCurrentDayAddBookRecord();
    }
}
