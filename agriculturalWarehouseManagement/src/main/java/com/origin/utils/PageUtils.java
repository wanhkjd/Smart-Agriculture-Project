package com.origin.utils;

import com.origin.common.PageResult;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页工具类
 * 提供内存分页功能，适用于小数据量场景
 */
public final class PageUtils {

    private PageUtils() {}

    /**
     * 内存分页
     * @param list     全量数据
     * @param page     页码（从1开始）
     * @param pageSize 每页条数
     * @return PageResult 分页结果
     */
    public static <T> PageResult<T> paginate(List<T> list, int page, int pageSize) {
        if (list == null || list.isEmpty()) {
            return PageResult.of(Collections.emptyList(), 0, page, pageSize);
        }
        if (pageSize <= 0) pageSize = 10;
        int total = list.size();
        int from = (page - 1) * pageSize;
        if (from >= total) {
            return PageResult.of(Collections.emptyList(), total, page, pageSize);
        }
        int to = Math.min(from + pageSize, total);
        return PageResult.of(list.subList(from, to), total, page, pageSize);
    }
}
