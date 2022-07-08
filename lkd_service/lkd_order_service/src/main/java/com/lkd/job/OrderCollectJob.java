package com.lkd.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lkd.common.VMSystem;
import com.lkd.entity.OrderCollectEntity;
import com.lkd.entity.OrderEntity;
import com.lkd.feignService.UserService;
import com.lkd.feignService.VMService;
import com.lkd.service.OrderCollectService;
import com.lkd.service.OrderService;
import com.lkd.viewmodel.PartnerViewModel;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCollectJob {

    private final OrderService orderService;
    private final OrderCollectService orderCollectService;
    private final VMService vmService;
    private final UserService userService;

    /**
     * 订单合并
     * @param param
     * @return
     */
    @XxlJob("orderCollectJobHandler")
    public ReturnT<String> collectTask(String param){

        var yesterday = LocalDate.now().plusDays(-1);

        var qw=new QueryWrapper<OrderEntity>();
        qw.select( "owner_id","node_id", "IFNULL(sum(amount),0) as amount ","IFNULL(sum(bill),0) as bill" ,"IFNULL(count(1),0) as price"     )
                .lambda().ge( OrderEntity::getCreateTime,yesterday  )
                .lt( OrderEntity::getCreateTime,LocalDate.now() )
                .eq(OrderEntity::getPayStatus , VMSystem.PAY_STATUS_PAYED)
                .groupBy(OrderEntity::getOwnerId,OrderEntity::getNodeId );

        orderService.list(qw).forEach( order->{
            var orderCollect=new OrderCollectEntity();
            orderCollect.setDate( yesterday );
            orderCollect.setNodeId( order.getNodeId() );
            orderCollect.setNodeName( vmService.getNodeName( order.getNodeId() ) );

            orderCollect.setOwnerId( order.getOwnerId() );
            var partner = userService.getPartner(order.getOwnerId());
            orderCollect.setOwnerName( partner.getName()   );

            orderCollect.setOrderTotalMoney( order.getAmount() );//金额
            orderCollect.setTotalBill( order.getBill() );//分成
            orderCollect.setOrderCount( order.getPrice() );//订单数量（借用了价格字段）
            orderCollect.setRatio( partner.getRatio() );//分成比例
            orderCollectService.save(orderCollect);

        } );
        return ReturnT.SUCCESS;

    }
    
    
}
