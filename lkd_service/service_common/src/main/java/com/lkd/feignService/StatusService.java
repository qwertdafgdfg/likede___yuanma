package com.lkd.feignService;

import com.lkd.feignService.fallback.StatusServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "status-service",fallbackFactory = StatusServiceFallbackFactory.class)
public interface StatusService {
    @GetMapping("/status/vmStatus/{innerCode}")
    Boolean getVMStatus(@PathVariable("innerCode") String innerCode);
    @GetMapping("/status/vmStatusMap/{innerCode}")
    Map<String,Boolean> getVMStatusMap(@PathVariable("innerCode") String innerCode);
    @GetMapping("/status/online/{innerCode}")
    void setOnline(@PathVariable String innerCode);
}
