package com.lkd.filter;

import com.google.common.base.Strings;
import com.lkd.common.VMSystem;
import com.lkd.config.GatewayConfig;
import com.lkd.http.view.TokenObject;
import com.lkd.service.UserService;
import com.lkd.utils.JWTUtil;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * JWT filter
 */
@Component
@Slf4j
public class JwtTokenFilter implements GlobalFilter, Ordered{

    @Autowired
    private GatewayConfig gatewayConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        boolean matchUrl = Arrays.stream(gatewayConfig.getUrls())
                .anyMatch(url::contains);
        if(matchUrl){
            return chain.filter(exchange);
        }
        if(null != gatewayConfig.getUrls()&& Arrays.asList(gatewayConfig.getUrls()).contains(url)){
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        ServerHttpResponse resp = exchange.getResponse();
        if(Strings.isNullOrEmpty(token)) return authError(resp);

        try {
            TokenObject tokenObject = JWTUtil.decode(token);
            JWTUtil.VerifyResult verifyResult = JWTUtil.verifyJwt(token,tokenObject.getMobile()+VMSystem.JWT_SECRET);
            if(!verifyResult.isValidate()) return authError(resp);
            //向headers中放用户id和登录类型
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("userId", tokenObject.getUserId()+"")
                    .header("loginType", tokenObject.getLoginType()+"")
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (IOException e) {
            return authError(resp);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 认证错误输出
     * @param resp 响应对象
     * @return
     */
    private Mono<Void> authError(ServerHttpResponse resp) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        String returnStr = "token校验失败";
        DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }
}
