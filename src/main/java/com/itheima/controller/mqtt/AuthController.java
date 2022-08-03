package com.itheima.controller.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**

 */
@RestController
@RequestMapping("/mqtt")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private HashMap<String,String> users;

    //初始化作用
    @PostConstruct
    public void init(){
        users = new HashMap<>();
        users.put("user","123456");
        users.put("emq-client2","123456");//testtopic/#
        users.put("emq-client3","123456");// testtopic/123
        users.put("admin","admin");
    }
    
    
    @PostMapping("/auth")
    public ResponseEntity auth(@RequestParam("clientid") String clientid,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password){
        
        log.info("emqx http认证组件开始调用任务服务完成认证,clientid={},username={},password={}",clientid,username,password);

        String value = users.get(username);
        if(StringUtils.isEmpty(value)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
        if(!value.equals(password)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    
    @PostMapping("/superuser")
    public ResponseEntity superuser(@RequestParam("clientid") String clientid,
                                    @RequestParam("username") String username){

        log.info("emqx 查询是否是超级用户,clientid={},username={}",clientid,username);
        
        if(clientid.contains("admin") || username.contains("admin")){
            log.info("用户{}是超级用户",username);
            return new ResponseEntity(HttpStatus.OK);
        }else {
            log.info("用户{}不是超级用户",username);
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
    }
    
    @PostMapping("/acl")
    public ResponseEntity acl(@RequestParam("access")int access,
                              @RequestParam("username")String username,
                              @RequestParam("clientid")String clientid,
                              @RequestParam("ipaddr")String ipaddr,
                              @RequestParam("topic")String topic,
                              @RequestParam("mountpoint")String mountpoint){
        log.info("EMQX发起客户端操作授权查询请求,access={},username={},clientid={},ipaddr={},topic={},mountpoint={}",
                access,username,clientid,ipaddr,topic,mountpoint);

        if(username.equals("emq-client2") && topic.equals("testtopic/#") && access  == 1){
            log.info("客户端{}有权限订阅{}",username,topic);
            return new ResponseEntity(HttpStatus.OK);
        }
        
        if(username.equals("emq-client3") && topic.equals("testtopic/123") && access == 2){
            log.info("客户端{}有权限向{}发布消息",username,topic);
            return new ResponseEntity(HttpStatus.OK);
        }

        //log.info("客户端{},username={},没有权限对主题{}进行{}操作",clientid,username,topic,access==1?"订阅":"发布");
        
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        //return new ResponseEntity(HttpStatus.OK);
    }
    
}
