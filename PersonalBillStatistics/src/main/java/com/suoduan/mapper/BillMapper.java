package com.suoduan.mapper;

import com.suoduan.entity.Bill;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface BillMapper {
    List<Bill> findByUserId(@Param("userId") Integer userId,
                            @Param("start") LocalDate start,
                            @Param("end") LocalDate end,
                            @Param("categoryId") Integer categoryId,
                            @Param("type") String type,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    long countByUserId(@Param("userId") Integer userId,
                       @Param("start") LocalDate start,
                       @Param("end") LocalDate end,
                       @Param("categoryId") Integer categoryId,
                       @Param("type") String type);

    Bill findById(@Param("id") Integer id);

    int insert(Bill bill);

    int update(Bill bill);

    int delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
