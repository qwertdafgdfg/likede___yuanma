package com.lkd.client;

import com.lkd.client.emq.EmqClient;
import com.lkd.client.mapper.ChannelMapper;
import com.lkd.client.mapper.SkuMapper;
import com.lkd.client.mapper.VersionMapper;
import com.lkd.client.pojo.Channel;
import com.lkd.client.emq.EmqInitServcie;
import com.lkd.client.pojo.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleTest {

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private VersionMapper versionMapper;


    @Autowired
    private EmqClient emqClient;

//    @Autowired
//    EmqInitServcie backServcie;

    @Test
    public void testSelect() {
//        for (int i = 0; i < 10; i++) {
//            Channel channel=new Channel();
//            channel.setSkuId("1111");
//            channel.setCapacity(3);
//            channelMapper.insert(channel);
//        }
//       //skuMapper.selectList(null);
//        List<Channel> list=channelMapper.selectList(null);
//        System.out.println(list);

        Version version= versionMapper.selectById(1);
        System.out.println(version);
        emqClient.publish("xxx","xxxx");

//        try {
//            backServcie.bak();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        //BackServcie.bak();
//        System.out.println(("----- selectAll method test ------"));
//        List<User> userList = userMapper.selectList(null);
//        Assert.assertEquals(5, userList.size());
//        userList.forEach(System.out::println);
    }
}
