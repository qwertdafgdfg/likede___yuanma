package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class RequestAllByDate{
    /**
     * 当前页码
     */
    private long pageIndex;
    /**
     * 当前页数据条数
     */
    private long pageSize;
}
