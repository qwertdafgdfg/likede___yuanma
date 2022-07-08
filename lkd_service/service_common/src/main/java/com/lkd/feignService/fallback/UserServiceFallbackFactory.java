package com.lkd.feignService.fallback;

import com.lkd.feignService.UserService;
import com.lkd.viewmodel.PartnerViewModel;
import com.lkd.viewmodel.UserViewModel;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserServiceFallbackFactory implements FallbackFactory<UserService>{
    @Override
    public UserService create(Throwable throwable) {
        log.error("用户服务调用失败",throwable);
        return new UserService() {
            @Override
            public UserViewModel getUser(int id) {
                return null;
            }

            @Override
            public List<UserViewModel> getRepairers(String regionId) {
                return null;
            }

            @Override
            public List<UserViewModel> getOperators(String regionId) {
                return null;
            }

            @Override
            public Integer getOperatorCount() {
                return 0;
            }

            @Override
            public Integer getRepairerCount() {
                return 0;
            }

            @Override
            public List<UserViewModel> getRepairerListByInnerCode(String innerCode) {
                return null;
            }

            @Override
            public List<UserViewModel> getOperatorListByInnerCode(String innerCode) {
                return null;
            }

            @Override
            public PartnerViewModel getPartner(Integer id) {
                return new PartnerViewModel();
            }

            @Override
            public Integer getCountByRegion(Long regionId, Boolean isRepair) {
                return 0;
            }

            @Override
            public String getPartnerName(Integer id) {
                return null;
            }
        };
    }
}
