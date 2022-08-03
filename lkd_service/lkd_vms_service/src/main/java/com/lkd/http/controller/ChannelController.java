package com.lkd.http.controller;

import com.lkd.entity.ChannelEntity;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.http.viewModel.SetChannelSkuReq;
import com.lkd.http.viewModel.VMChannelConfig;
import com.lkd.service.ChannelService;
import com.lkd.service.PolicyService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    @Lazy
    private ChannelService channelService;
    @Autowired
    @Lazy
    private PolicyService policyService;


//    /**
//     * 新增
//     * @param channel
//     * @return 是否成功
//     */
//    @PostMapping
//    public boolean add(@RequestBody ChannelEntity channel){
//        return channelService.save( channel );
//    }

//    /**
//     * 修改
//     * @param channelId
//     * @param channel
//     * @return 是否成功
//     */
//    @PutMapping("/{channelId}")
//    public boolean update(@PathVariable Long channelId,@RequestBody ChannelEntity channel){
//        channel.setChannelId( channelId );
//        return channelService.updateById( channel );
//    }


    /**
     * 分页查询
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @param searchMap 条件
     * @return 分页结果
     */
    @GetMapping("/page/{pageIndex}/{pageSize}")
    public Pager<ChannelEntity> findPage(@PathVariable long pageIndex, @PathVariable long pageSize, @RequestParam Map searchMap){
        return channelService.findPage( pageIndex,pageSize,searchMap );
    }

    @GetMapping("/channelList/{innerCode}")
    public List<ChannelEntity> getChannelList(@PathVariable("innerCode") String innerCode){
        return channelService.getChannelesByInnerCode(innerCode);
    }

    @GetMapping("/channelInfo/{innerCode}/{channelCode}")
    public ChannelEntity getChannelInfo(@PathVariable("innerCode") String innerCode,@PathVariable("channelCode") String channelCode){
        return channelService.getChannelInfo(innerCode,channelCode);
    }

    @PutMapping("/setSku")
    public boolean setSku(@RequestBody SetChannelSkuReq req){
        VmPolicyEntity vmPolicyEntity = policyService.getPolicyByInnerCode(req.getInnerCode());
        Integer policyId = 0;
        if(vmPolicyEntity != null)
            policyId = vmPolicyEntity.getPolicyId();

        return  channelService.mapSku(req.getInnerCode(),req.getChannelCode(),Long.valueOf(req.getSkuId()),policyId);
    }

    /**
     * 货道配置
     * @param channelConfig
     * @return
     */
    @PutMapping("/channelConfig")
    public Boolean setChannel(@RequestBody VMChannelConfig channelConfig){
        return channelService.mapSku(channelConfig);
    }
}
