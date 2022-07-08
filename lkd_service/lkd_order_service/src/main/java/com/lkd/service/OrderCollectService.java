package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.OrderCollectEntity;

import java.time.LocalDate;
import java.util.List;

public interface OrderCollectService extends IService<OrderCollectEntity> {
    /**
     *获取一定时间范围内的合作商的点位分成数据
     * @param start
     * @param end
     * @return
     */
    List<OrderCollectEntity> getOwnerCollectByDate(Integer ownerId,LocalDate start,LocalDate end);

//    /**
//     * 获取某一公司在一定时间内的销售数据
//     * @param companyId
//     * @param start
//     * @param end
//     * @return
//     */
//    List<OrderCollectEntity> getCompanyTrend(int companyId,LocalDate start,LocalDate end);
//
//    /**
//     * 获取某一地区
//     * @param start
//     * @param end
//     * @return
//     */
//    List<OrderCollectEntity> getAreaCollectByData(int areaId,LocalDate start,LocalDate end);

//    /**
//     * 获取一定时间范围内的销量前15的商品
//     * @param start
//     * @param end
//     * @return
//     */
//    List<OrderEntity> getTop15Skus(LocalDateTime start, LocalDateTime end);

//    /**
//     * 获取一定时间范围之内的汇总信息
//     * @param start
//     * @param end
//     * @return
//     */
//    OrderCollectEntity getCollectInfo(LocalDate start, LocalDate end);
}
