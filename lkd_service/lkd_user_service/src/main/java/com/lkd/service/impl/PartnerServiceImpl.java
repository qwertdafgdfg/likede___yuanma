package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.lkd.common.VMSystem;
import com.lkd.dao.PartnerDao;
import com.lkd.entity.PartnerEntity;
import com.lkd.exception.LogicException;
import com.lkd.feignService.VMService;
import com.lkd.http.view.TokenObject;
import com.lkd.http.viewModel.LoginReq;
import com.lkd.http.viewModel.LoginResp;
import com.lkd.http.viewModel.PartnerReq;
import com.lkd.http.viewModel.PartnerUpdatePwdReq;
import com.lkd.service.PartnerService;
import com.lkd.utils.BCrypt;
import com.lkd.utils.JWTUtil;
import com.lkd.viewmodel.Pager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerServiceImpl extends ServiceImpl<PartnerDao, PartnerEntity> implements PartnerService {
    private final RedisTemplate<String,String> redisTemplate;

//    @Autowired
//    private MqttProducer mqttProducer;

    private final VMService vmService;
    @Override
    public LoginResp login(LoginReq req) throws IOException {
        LoginResp resp = new LoginResp();
        resp.setSuccess(false);
        String code = redisTemplate.opsForValue().get(req.getClientToken());
        if(Strings.isNullOrEmpty(code)){
            resp.setMsg("验证码错误");
            return resp;
        }
        if(!req.getCode().equals(code)){
            resp.setMsg("验证码错误");
            return resp;
        }
        QueryWrapper<PartnerEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(PartnerEntity::getAccount,req.getAccount());
        PartnerEntity partnerEntity = this.getOne(qw);

        if(partnerEntity == null){
            resp.setMsg("不存在该账户");
            return resp;
        }

        if(!BCrypt.checkpw(req.getPassword(),partnerEntity.getPassword())){
            resp.setMsg("账号或密码错误");
            return resp;
        }

        resp.setSuccess(true);
        resp.setUserName(partnerEntity.getName());
        resp.setUserId(partnerEntity.getId());
        resp.setMsg("登录成功");

        TokenObject tokenObject = new TokenObject();
        tokenObject.setUserId(partnerEntity.getId());
        //tokenObject.setUserName(partnerEntity.getName());
        tokenObject.setLoginType(VMSystem.LOGIN_EMP);
        tokenObject.setMobile(partnerEntity.getMobile());
        String token = JWTUtil.createJWTByObj(tokenObject,partnerEntity.getMobile() + VMSystem.JWT_SECRET);
        resp.setToken(token);

        return resp;
    }

    @Override
    public Boolean modify(Integer id, PartnerReq req) {
        var uw = new LambdaUpdateWrapper<PartnerEntity>();
        uw
                .set(PartnerEntity::getName,req.getName())
                .set(PartnerEntity::getRatio,req.getRatio())
                .set(PartnerEntity::getContact,req.getContact())
                .set(PartnerEntity::getPhone,req.getPhone());
        PartnerEntity partnerEntity = new PartnerEntity();
        BeanUtils.copyProperties(req,partnerEntity);
        partnerEntity.setId(id);

        return this.updateById(partnerEntity);

//        PartnerUpdate partnerUpdate = new PartnerUpdate();
//        partnerUpdate.setId(partnerEntity.getId());
//        partnerUpdate.setName(partnerEntity.getName());
//        try {
//            mqttProducer.send("server/updatePartner", 2,JsonUtil.serialize(partnerUpdate));
//        }catch (JsonProcessingException e){
//            log.error("发送合作商变更消息失败。",e);
//        }
//        return true;
    }

    @Override
    public boolean delete(Integer id) {
        Integer nodeCount = vmService.getNodeCountByOwnerId(id);
        if(nodeCount == null)
            throw new LogicException("无法获取所属点位数");
        if(nodeCount > 0){
            throw new LogicException("请先修改下属点位归属");
        }

        return this.removeById(id);
    }

    @Override
    public void resetPwd(Integer id) {
        String pwd = BCrypt.hashpw("123456",BCrypt.gensalt());
        var uw = new LambdaUpdateWrapper<PartnerEntity>();
        uw
                .set(PartnerEntity::getPassword,pwd)
                .eq(PartnerEntity::getId,id);

        this.update(uw);
    }

    @Override
    public Pager<PartnerEntity> search(Long pageIndex, Long pageSize, String name) {
        Page<PartnerEntity> page = new Page<>(pageIndex,pageSize);
        LambdaQueryWrapper<PartnerEntity> qw = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(name)){
            qw.like(PartnerEntity::getName,name);
        }
        this.page(page,qw);
        page.getRecords().forEach(p->{
            p.setPassword("");
            p.setVmCount(vmService.getVmCountByOwnerId(p.getId()));
        });

        return Pager.build(page);
    }

    @Override
    public Boolean updatePwd(Integer id,PartnerUpdatePwdReq req) {
        var partner = this.getById(id);
        if(partner == null){
            throw new LogicException("合作商不存在");
        }
        if(!BCrypt.checkpw(req.getPassword(),partner.getPassword())){
            throw new LogicException("原始密码错误");
        }
        var uw = new LambdaUpdateWrapper<PartnerEntity>();
        uw
                .set(PartnerEntity::getPassword,BCrypt.hashpw(req.getPassword(),BCrypt.gensalt()))
                .eq(PartnerEntity::getId,id);

        return this.update(uw);
    }
}
