package com.lkd.feignService.fallback;

import com.lkd.feignService.StatusService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class StatusServiceFallbackFactory implements FallbackFactory<StatusService> {
    @Override
    public StatusService create(Throwable throwable) {
        log.error("状态服务调用失败",throwable);
        return new StatusService() {
            @Override
            public Boolean getVMStatus(String innerCode) {
                return false;
            }

            @Override
            public Map<String, Boolean> getVMStatusMap(String innerCode) {
                return null;
            }

            @Override
            public void setOnline(String innerCode) {

            }
        };
    }
}
