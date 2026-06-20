package com.suoduan.mapper;

import com.suoduan.entity.MonthCompare;
import com.suoduan.entity.MonthStat;
import com.suoduan.entity.StatItem;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsMapper {

    List<StatItem> categorySum(@Param("userId") Integer userId,
                               @Param("start") LocalDate start,
                               @Param("end") LocalDate end,
                               @Param("type") String type);

    List<MonthStat> monthlySum(@Param("userId") Integer userId,
                               @Param("type") String type,
                               @Param("year") String year);

    List<MonthCompare> monthlyCompare(@Param("userId") Integer userId,
                                      @Param("year") String year);
}
