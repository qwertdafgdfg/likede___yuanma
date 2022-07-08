package com.lkd.generateTable;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GenerateSqlUtilsTest{

    @Test
    public void generate() {
        SqlBean sqlbean = new SqlBean();

        List<String> monthList= GenerateSqlUtils.getMonthBetween(LocalDate.of(2020,1,1),LocalDate.of(2020,10,1));
        System.out.println(monthList);
        sqlbean.setOrderMonthList(monthList);
        Map<String, Object> paramMp = Maps.newHashMap();
        paramMp.put("sqlbean", sqlbean);
        GenerateSqlUtils.generate("db_date.ftl", paramMp,"sql_month.sql");
    }

    @Test
    public void testCal(){
        BigDecimal bg = new BigDecimal(200);
        int bill = bg.multiply(new BigDecimal(60)).divide(new BigDecimal(100),0,BigDecimal.ROUND_HALF_UP).intValue();

        System.out.println(bill);
    }
}