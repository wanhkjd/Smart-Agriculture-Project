package com.suoduan.service;

import com.suoduan.entity.MonthCompare;
import com.suoduan.entity.MonthStat;
import com.suoduan.entity.StatItem;
import com.suoduan.mapper.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsMapper reportMapper;

    @Autowired
    public StatisticsService(StatisticsMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public List<MonthCompare> monthlyCompare(Integer userId, String year) {
        return reportMapper.monthlyCompare(userId, year);
    }

    public List<MonthStat> monthlySum(Integer userId, String type, String year) {
        return reportMapper.monthlySum(userId, type, year);
    }

    public List<StatItem> categorySum(Integer userId, LocalDate start, LocalDate end, String type) {
        return reportMapper.categorySum(userId, start, end, type);
    }
}
