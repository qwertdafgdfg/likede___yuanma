package com.lkd.service.impl;

import com.lkd.service.NodeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NodeServiceImplTest{

    @Autowired
    private NodeService nodeService;
    @Test
    public void findByArea() {
//        Pager<NodeEntity> result = nodeService.findByArea(3,1,10);

        Assert.assertTrue(true);
    }


    @Test
    public void test(){
        int maxCapacity=20;
        int percent=65;
        int alert=   (int)(maxCapacity  *  (float)percent/100 );
        System.out.println(alert);


    }

}
