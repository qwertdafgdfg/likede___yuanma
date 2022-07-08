package com.lkd.http.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Strings;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.service.VendingMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/acl")
@RequiredArgsConstructor
@Slf4j
public class AclController {

    private final VendingMachineService vmService;


    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestParam(value = "clientid",defaultValue = "") String clientid,
                                  @RequestParam(value = "username",defaultValue = "") String username,
                                  @RequestParam(value = "password",defaultValue = "") String password ){
        log.info("客户端连接认证"+clientid);
        if(Strings.isNullOrEmpty(clientid )){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if(clientid.startsWith("mqtt")){
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        if(clientid.startsWith("monitor")){ //如果是微服务
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        //到售货机表中查询 clientid
        var qw=new LambdaQueryWrapper<VendingMachineEntity>();
        qw.eq(VendingMachineEntity::getClientId,clientid);
        var vm = vmService.getOne(qw);
        if(vm==null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null,HttpStatus.OK);
    }


    /**
     * 超级用户认证
     * @param clientid
     * @param username
     * @return
     */
    @PostMapping("/superuser")
    public ResponseEntity<?> superuser(@RequestParam(value = "clientid",defaultValue = "") String clientid,
                                       @RequestParam(value = "username",defaultValue = "") String username){
        log.info("超级用户认证");
        if(clientid.startsWith("mqtt")){
            log.info(clientid+"是超级用户");
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        if(clientid.startsWith("monitor")){ //如果是微服务
            log.info(clientid+"是超级用户");
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        log.info(clientid+"不是超级用户");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    /**
     * ACL 发布订阅控制
     * @param clientid
     * @param access
     * @param topic
     * @return
     */
    @PostMapping("/pubsub")
    public ResponseEntity<?> pubsub(@RequestParam(value = "clientid",defaultValue = "") String clientid,
                                    @RequestParam  int access,
                                    @RequestParam(value = "topic",defaultValue = "")    String topic){

        log.info("acl发布订阅控制  clientid:"+clientid+"  access:"+access+"  topic:"+topic);

        if(Strings.isNullOrEmpty(clientid )){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //到售货机表中查询 clientid
        var qw=new LambdaQueryWrapper<VendingMachineEntity>();
        qw.eq(VendingMachineEntity::getClientId,clientid);
        var vm = vmService.getOne(qw);
        if(vm==null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //发布
        if(access==2 && topic.equals("server/"+vm.getInnerCode() )){
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        //订阅
        if(access==1 && topic.equals("vm/"+vm.getInnerCode() )){
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        log.info(clientid+"不能发布订阅消息");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    }





}
