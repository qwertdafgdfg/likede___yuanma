package com.lkd.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lkd.common.VMSystem;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.service.VendingMachineService;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VendingMachineServiceImplTest{

    @Autowired
    private VendingMachineService vmService;
    @Autowired
    private RestHighLevelClient esClient;

    @Test
    public void findByInnerCode() {
        VendingMachineEntity vendingMachineEntity = vmService.findByInnerCode("1111111");
        //QueryWrapper<VendingMachineEntity> qw = new QueryWrapper<>();


        Assert.assertNotNull(vendingMachineEntity);
    }

    @Test
    public void mod(){
        QueryWrapper<VendingMachineEntity> qw = new QueryWrapper<>();
        qw
                .lambda()
                .eq(VendingMachineEntity::getVmStatus, VMSystem.VM_STATUS_RUNNING)

                .apply("mod(id,"+2+") = " + 1);
        List<VendingMachineEntity> result = vmService.list(qw);

        System.out.println(result);
    }

    @Test
    public void updateES(){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.field("city","新市区1");
            builder.endObject();
            UpdateRequest updateRequest = new UpdateRequest("vm","01000001")
                    .doc("typeName","饮料机");

//            updateRequest.doc(XContentFactory
//                    .jsonBuilder()
//                    .startObject()
//                    .field("city","新市区1")
//                    .endObject());

            UpdateResponse response = esClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
