package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.VendoutRunningDao;
import com.lkd.entity.VendoutRunningEntity;
import com.lkd.service.VendoutRunningService;
import com.lkd.viewmodel.Pager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VendoutRunningServiceImpl extends ServiceImpl<VendoutRunningDao,VendoutRunningEntity> implements VendoutRunningService{
    @Override
    public Pager<VendoutRunningEntity> findPage(long pageIndex, long pageSize, Map searchMap) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VendoutRunningEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        QueryWrapper queryWrapper = createQueryWrapper( searchMap );
        this.page(page,queryWrapper);

        Pager<VendoutRunningEntity> pageResult = new Pager<>();
        pageResult.setCurrentPageRecords(page.getRecords());
        pageResult.setPageIndex(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotalCount(page.getTotal());
        return pageResult;
    }

    @Override
    public List<VendoutRunningEntity> findList(Map searchMap) {
        QueryWrapper queryWrapper = createQueryWrapper( searchMap );
        return this.list(queryWrapper);
    }

    /**
     * 条件构建
     * @param searchMap
     * @return
     */
    private QueryWrapper createQueryWrapper(Map searchMap){
        QueryWrapper queryWrapper=new QueryWrapper(  );
        if(searchMap!=null){
            queryWrapper.allEq(searchMap);
        }
        return queryWrapper;
    }

}
