package com.origin.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int pageSize) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        return new PageResult<>(records, total, page, pageSize, totalPages);
    }

    /**
     * 内存分页：适用于小数据量场景
     */
    public static <T> PageResult<T> paginate(List<T> allRecords, int page, int pageSize) {
        if (pageSize <= 0) pageSize = 10;
        int total = allRecords.size();
        int from = (page - 1) * pageSize;
        if (from >= total) {
            return of(Collections.emptyList(), total, page, pageSize);
        }
        int to = Math.min(from + pageSize, total);
        return of(allRecords.subList(from, to), total, page, pageSize);
    }
}
