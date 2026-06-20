package com.suoduan.controller;

import com.suoduan.entity.MonthCompare;
import com.suoduan.entity.MonthStat;
import com.suoduan.entity.Result;
import com.suoduan.entity.StatItem;
import com.suoduan.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService reportService;

    @Autowired
    public StatisticsController(StatisticsService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly-compare")
    public Result<List<MonthCompare>> monthlyCompare(
            @RequestParam String year,
            HttpSession session) {
        List<MonthCompare> compareRows = reportService.monthlyCompare(sessionUserId(session), year);
        return Result.ok(compareRows);
    }

    @GetMapping("/category")
    public Result<List<StatItem>> categorySum(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "expense") String type,
            HttpSession session) {
        List<StatItem> totals = reportService.categorySum(sessionUserId(session), start, end, type);
        return Result.ok(totals);
    }

    @GetMapping("/monthly")
    public Result<List<MonthStat>> monthlySum(
            @RequestParam String year,
            @RequestParam(defaultValue = "expense") String type,
            HttpSession session) {
        List<MonthStat> trendRows = reportService.monthlySum(sessionUserId(session), type, year);
        return Result.ok(trendRows);
    }

    private Integer sessionUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
