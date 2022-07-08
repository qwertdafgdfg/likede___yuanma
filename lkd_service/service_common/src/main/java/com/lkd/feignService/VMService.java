package com.lkd.feignService;

import com.lkd.feignService.fallback.VmServiceFallbackFactory;
import com.lkd.viewmodel.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "vm-service",fallbackFactory = VmServiceFallbackFactory.class)
public interface VMService{
    @GetMapping("/node/countForOwner/{ownerId}")
    Integer getNodeCountByOwnerId(@PathVariable("ownerId") Integer ownerId);

    @GetMapping("/vm/countByOwner/{ownerId}")
    Integer getVmCountByOwnerId(@PathVariable("ownerId") Integer ownerId);

    @GetMapping("/vm/info/{innerCode}")
    VendingMachineViewModel getVMInfo(@PathVariable String innerCode);

    @GetMapping("/vm/inventory/{percent}")
    void inventory(@PathVariable int percent);

    @GetMapping("/vm/skuList/{innerCode}")
    List<SkuViewModel> getAllSkuByInnerCode(@PathVariable String innerCode);
    @GetMapping("/vm/sku/{innerCode}/{skuId}")
    SkuViewModel getSku(@PathVariable String innerCode,@PathVariable String skuId);
    @GetMapping("/sku/skuViewModel/{skuId}")
    SkuViewModel getSkuById(@PathVariable long skuId);

    @GetMapping("/region/regionInfo/{regionId}")
    RegionViewModel getRegionById(@PathVariable String regionId);

    @GetMapping("/node/nodeName/{id}")
    String getNodeName(@PathVariable Long id);

    @GetMapping("/vm/hasCapacity/{innerCode}/{skuId}")
    Boolean hasCapacity(@PathVariable String innerCode,@PathVariable Long skuId);


    /**
     * 搜索附近售货机
     * @param vmSearch
     * @return
     */
    @PostMapping("/vm/search")
    public List<VmInfoDTO> search(@RequestBody VmSearch vmSearch );


}
