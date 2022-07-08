package com.lkd.generateTable;

import lombok.Data;

import java.util.List;

@Data
public class SqlBean{
    /**
     * 按月分表
     */
    private List<String> orderMonthList;
}
