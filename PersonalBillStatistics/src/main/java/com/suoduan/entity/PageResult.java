package com.suoduan.entity;

import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public PageResult(List<T> records, long totalRows, int currentPage, int size) {
        this.list = records;
        this.total = totalRows;
        this.page = currentPage;
        this.pageSize = size;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
