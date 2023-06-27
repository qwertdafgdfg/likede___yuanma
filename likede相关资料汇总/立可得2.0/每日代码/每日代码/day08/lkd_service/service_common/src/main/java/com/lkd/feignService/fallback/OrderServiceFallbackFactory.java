package com.lkd.feignService.fallback;
import com.lkd.feignService.OrderService;
import com.lkd.viewmodel.OrderViewModel;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.RequestPay;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderServiceFallbackFactory implements FallbackFactory<OrderService> {
    @Override
    public OrderService create(Throwable throwable) {
        log.error("订单服务调用失败",throwable);
        return new OrderService() {
            @Override
            public String requestPay(RequestPay requestPay) {
                return null;
            }

            @Override
            public Boolean cancel(String orderNo) {
                return null;
            }

            @Override
            public Pager<OrderViewModel> search(Integer pageIndex, Integer pageSize, String orderNo, String openId, String startDate, String endDate) {
                return Pager.buildEmpty();
            }

        };
    }
}
