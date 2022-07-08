package com.lkd.http.controller;
import com.lkd.entity.NodeEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.exception.LogicException;
import com.lkd.http.viewModel.NodeReq;
import com.lkd.service.NodeService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;


    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public NodeEntity findById(@PathVariable String id){
        return nodeService.getById(Long.valueOf(id));
    }

    /**
     * 获取点位名称
     * @param id
     * @return
     */
    @GetMapping("/nodeName/{id}")
    public String getNodeName(@PathVariable Long id){
        return nodeService.getById(id).getName();
    }

    /**
     * 修改
     * @param id
     * @param req
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody NodeReq req) throws LogicException {
        NodeEntity nodeEntity  = this.convert(req);;
        nodeEntity.setId(id);

        return nodeService.update(nodeEntity);
    }

    /**
     * 删除
     * @param id
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public  boolean delete(@PathVariable Long id){
        return nodeService.delete( id );
    }


//    /**
//     * 通过区域Id获取其下所有点位信息
//     * @param areaId
//     * @param pageIndex
//     * @param pageSize
//     * @return
//     */
//    @GetMapping("/pageByArea/{areaId}/{pageIndex}/{pageSize}")
//    public Pager<NodeEntity> findByAreaId(@PathVariable Integer areaId,
//                                          @PathVariable long pageIndex,
//                                          @PathVariable long pageSize){
//        return nodeService.findByArea(areaId,pageIndex,pageSize);
//    }


    /**
     * 搜索点位
     * @param page
     * @param paseSize
     * @param name
     * @param regionId
     * @return
     */
    @GetMapping("/search")
    public Pager<NodeEntity> query(@RequestParam(value = "page",required = false,defaultValue = "1") Long page,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long paseSize,
                                   @RequestParam(value = "name",required = false) String name,
                                   @RequestParam(value = "regionId",required = false) String regionId
                                  ){
        return nodeService.search(name,regionId,page,paseSize);
    }

    /**
     * 创建点位
     * @param req
     * @return
     * @throws LogicException
     */
    @PostMapping
    public boolean createNode(@RequestBody NodeReq req) throws LogicException {
        NodeEntity nodeEntity = this.convert(req);

        return nodeService.add(nodeEntity);
    }

    /**
     * 获取点位下所有售货机列表
     * @param nodeId
     * @return
     */
    @GetMapping("/vmList/{nodeId}")
    public List<VendingMachineEntity> getAllVms(@PathVariable("nodeId") long nodeId){
        return nodeService.getVmList(nodeId);
    }

    /**
     * 获取合作商下点位数
     * @param ownerId
     * @return
     */
    @GetMapping("/countForOwner/{ownerId}")
    public Integer getCountByOwner(@PathVariable Integer ownerId){
        return nodeService.getCountByOwner(ownerId);
    }

    private NodeEntity convert(NodeReq req){
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setName(req.getName());
        nodeEntity.setAreaId(req.getAreaId());
        nodeEntity.setAddr(req.getAddr());
        nodeEntity.setBusinessId(req.getBusinessId());
        nodeEntity.setRegionId(Long.valueOf(req.getRegionId()));
        nodeEntity.setCreateUserId(req.getCreateUserId());
        nodeEntity.setOwnerId(req.getOwnerId());

        return nodeEntity;
    }
}
