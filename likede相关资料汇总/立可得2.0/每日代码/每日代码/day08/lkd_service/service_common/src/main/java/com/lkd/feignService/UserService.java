package com.lkd.feignService;

import com.lkd.feignService.fallback.UserServiceFallbackFactory;
import com.lkd.viewmodel.PartnerViewModel;
import com.lkd.viewmodel.UserViewModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "user-service",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService{
    @GetMapping("/user/{id}")
    UserViewModel getUser(@PathVariable("id") int id);
    @GetMapping("/user/repairers/{regionId}")
    List<UserViewModel> getRepairers(@PathVariable("regionId") String regionId);
    @GetMapping("/user/operators/{regionId}")
    List<UserViewModel> getOperators(@PathVariable("regionId") String regionId);

    @GetMapping("/user/operaterCount")
    Integer getOperatorCount();

    @GetMapping("/user/repairerCount")
    Integer getRepairerCount();

    @GetMapping("/user/repairerList/{innerCode}")
    List<UserViewModel> getRepairerListByInnerCode(@PathVariable String innerCode);

    @GetMapping("/user/operatorList/{innerCode}")
    List<UserViewModel> getOperatorListByInnerCode(@PathVariable String innerCode);

    @GetMapping("/partner/{id}")
    PartnerViewModel getPartner(@PathVariable Integer id);

    @GetMapping("/user/countByRegion/{regionId}/{isRepair}")
    Integer getCountByRegion(@PathVariable Long regionId,@PathVariable Boolean isRepair);

    @GetMapping("/partner/name")
    String getPartnerName(@PathVariable Integer id);
}
