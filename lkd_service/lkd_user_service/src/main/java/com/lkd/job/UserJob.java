package com.lkd.job;

import com.lkd.common.VMSystem;
import com.lkd.entity.UserEntity;
import com.lkd.service.UserService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class UserJob {

    /*
    @XxlJob("testHandler")
    public ReturnT<String> testHandler(String param) throws Exception {

        log.info("立可得集成xxl-job");
        return ReturnT.SUCCESS;
    }
    */


    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 每日工单量列表初始化
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("workCountInitJobHandler")
    public ReturnT<String> workCountInitJobHandler(String param) throws Exception{
        //查询用户列表
        List<UserEntity> userList = userService.list();

        //构建数据（zset）
        userList.forEach(user -> {
            if(user.getRoleId().intValue()!=0){ //只考虑非管理员
                String key= VMSystem.REGION_TASK_KEY_PREF
                        + LocalDate.now().plusDays(0).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        +"."+ user.getRegionId()+"."+user.getRoleCode();
                redisTemplate.opsForZSet().add(key,user.getId(),0 ) ;
                XxlJobLogger.log("初始化"+key+":"+user.getId());
                redisTemplate.expire(key, Duration.ofDays(2));
            }
        });
        return ReturnT.SUCCESS;
    }

}
