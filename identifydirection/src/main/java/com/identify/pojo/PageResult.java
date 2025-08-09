package com.identify.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total;      // 总记录数
    private int pages;       // 总页数
    private int pageSize;    // 每页结果数
    private int currentPage; // 当前页码
    private List<T> data;    // 分页数据列表
}