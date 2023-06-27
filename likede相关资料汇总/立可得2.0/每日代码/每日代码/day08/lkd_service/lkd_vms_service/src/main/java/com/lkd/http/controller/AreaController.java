package com.lkd.http.controller;
import com.lkd.entity.AreaEntity;
import com.lkd.service.AreaService;
import com.lkd.viewmodel.AreaViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/area")
@CacheConfig(cacheNames = "area")
public class AreaController {

    @Autowired
    private AreaService areaService;

    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public AreaViewModel findById(@PathVariable Integer id){
        return convertToVm(areaService.getById(id));
    }

    /**
     * 获取所有根区域
     * @return
     */
    @GetMapping("/roots")
    @Cacheable(cacheNames = "rootAreas",key = "targetClass + methodName")
    public List<AreaViewModel> getAllRoot(){
        return areaService.getAllRootAreaList().stream()
                .map(a-> convertToVm(a))
                .collect(Collectors.toList());
    }

    /**
     * 获取下一级子区域
     * @param parentID
     * @return
     */
    @GetMapping("/children/{parentId}")
    @Cacheable(cacheNames = "children",key = "targetClass + methodName + #p0")
    public List<AreaViewModel> getAllChildren(@PathVariable("parentId") int parentID){
        return areaService.getAllChildren(parentID)
                .stream()
                .map(a->convertToVm(a))
                .collect(Collectors.toList());
    }

    private AreaViewModel convertToVm(AreaEntity entity){
        AreaViewModel area = new AreaViewModel();
        area.setAdCode(entity.getAdCode());
        area.setAreaName(entity.getAreaName());
        area.setId(entity.getId());
        area.setAreaLevel(entity.getAreaLevel());
        area.setParentId(entity.getParentId());

        return area;
    }
}
