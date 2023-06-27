package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.contract.VendoutResp;
import com.lkd.entity.OrderEntity;
import com.lkd.http.viewModel.CreateOrderReq;
import com.lkd.http.viewModel.OrderResp;
import com.lkd.viewmodel.CreateOrder;
import com.lkd.viewmodel.OrderViewModel;
import com.lkd.viewmodel.Pager;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService extends IService<OrderEntity> {


    /**
     * 微信小程序支付创建订单
     * @param createOrder
     * @return
     */
    OrderEntity createOrder(CreateOrder createOrder);


    /**
     * 处理出货结果
     * @param vendoutResp 出货请求参数
     * @return
     */
    boolean vendoutResult(VendoutResp vendoutResp);


    /**
     * 支付完成
     * @param orderNo
     * @return
     */
    boolean payComplete(String orderNo);

    /**
     * 通过订单编号获取订单实体
     * @param orderNo
     * @return
     */
    OrderEntity getByOrderNo(String orderNo);

    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    Boolean cancel(String orderNo);


    /**
     * 订单搜索
     * @param pageIndex
     * @param pageSize
     * @param orderNo
     * @param openId
     * @param startDate
     * @param endDate
     * @return
     */
    Pager<OrderViewModel> search(Integer pageIndex,Integer pageSize,String orderNo,String openId,String startDate,String endDate);


    /**
     * 获取商圈下销量最好的10个商品
     * @param businessId
     * @return
     */
    List<Long> getTop10Sku( Integer businessId );



    /**
     * 获取一定时间范围之内的金额
     * @param partnerId
     * @param start
     * @param end
     * @return
     */
    Long getAmount(Integer partnerId, String innerCode,LocalDateTime start, LocalDateTime end);

}
