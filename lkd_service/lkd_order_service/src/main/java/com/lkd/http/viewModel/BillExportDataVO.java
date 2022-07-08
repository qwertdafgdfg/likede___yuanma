package com.lkd.http.viewModel;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

@Data
@ContentRowHeight(15)
@HeadRowHeight(20)
@ColumnWidth(25)
public class BillExportDataVO {

    @DateTimeFormat("yyyy年MM月dd日")
    @ExcelProperty(value = "分账日期",index = 0)
    private Date date;

    @ExcelProperty(value = "分账点位",index = 1)
    private String nodeName;

    @ExcelProperty(value = "订单数",index = 2)
    private Integer orderCount;

    @ExcelProperty(value = "分账金额",index = 3)
    private Integer amount;

}
