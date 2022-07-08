package com.lkd.service;

import com.lkd.entity.OrderCollectEntity;
import com.lkd.viewmodel.BarCharCollect;
import com.lkd.viewmodel.Pager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface ReportService {


    /**
     * 获取合作商分账汇总信息
     * @param pageIndex
     * @param pageSize
     * @param name
     * @param start
     * @param end
     * @return
     */
    Pager<OrderCollectEntity> getPartnerCollect(Long pageIndex, Long pageSize, String name, LocalDate start ,LocalDate end );


    /**
     * 获取合作商前12条点位分账数据
     * @param partnerId
     * @return
     */
    List<OrderCollectEntity> getTop12(Integer partnerId);


    /**
     * 合作商点位分账搜索
     * @param partnerId
     * @param nodeName
     * @param start
     * @param end
     * @return
     */
    Pager<OrderCollectEntity> search(Long pageIndex,Long pageSize,Integer partnerId, String nodeName, LocalDate start, LocalDate end);


    /**
     * 分账数据列表
     * @param partnerId
     * @param nodeName
     * @param start
     * @param end
     * @return
     */
    List<OrderCollectEntity> getList( Integer partnerId, String nodeName, LocalDate start, LocalDate end );


    /**
     * 获取一定日期内合作商收益
     * @param partnerId
     * @param start
     * @param end
     * @return
     */
    BarCharCollect getCollect(Integer partnerId,LocalDate start ,LocalDate end );


    /**
     * 获取一定日期内的销售额趋势
     * @param collectType
     * @param start
     * @param end
     * @return
     */
    BarCharCollect getAmountCollect(Integer collectType,LocalDate start ,LocalDate end );


    /**
     * 获取区域销量统计
     * @param start
     * @param end
     * @return
     */
    BarCharCollect getCollectByRegion(LocalDate start ,LocalDate end );

}
