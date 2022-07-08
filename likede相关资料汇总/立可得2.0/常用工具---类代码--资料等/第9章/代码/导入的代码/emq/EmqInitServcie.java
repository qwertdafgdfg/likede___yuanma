package com.lkd.client.emq;

import com.alibaba.fastjson.JSON;
import com.lkd.client.config.ApplicationContextRegister;
import com.lkd.client.config.EmqConfig;
import com.lkd.client.config.MsgType;
import com.lkd.client.emq.msg.VersionReq;
import com.lkd.client.mapper.VersionMapper;
import com.lkd.client.pojo.Version;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;


/**
 * emq客户端采用嵌入式数据库h2
 */
@Slf4j
@Service
@AutoConfigureAfter(DataSource.class) //DataSource创建完后才初始化此类
public class EmqInitServcie {

    //初始化sql
    private static final String schema="classpath:db/schema-h2.sql";

    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationContextRegister applicationContext;

    @Autowired
    EmqConfig config;

    @Autowired
    private EmqMsgCallback emqMsgCallback;

    @Autowired
    private EmqClient emqClient;



    @PostConstruct
    public  void init() throws Exception {
        //初始化本地数据库
        String userHome= System.getProperty("user.home");//获取系统用户目录
        File f = new File(userHome+File.separator+"lkd.lock");
        if(!f.exists()){
            log.info("--------------初始化h2数据----------------------");
            f.createNewFile();
            Resource resource= (Resource) applicationContext.getResource(schema);
            ScriptUtils.executeSqlScript(dataSource.getConnection(),resource);
        }
        //建立mqtt连接
        emqClient.connect(emqMsgCallback);
    }
}
