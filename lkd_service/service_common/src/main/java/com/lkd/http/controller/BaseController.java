package com.lkd.http.controller;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
/**
 * controller父类
 * @author 61941
 */
public class BaseController {

    @Autowired
    private HttpServletRequest request; //自动注入request

    /**
     * 返回用户ID
     * @return
     */
    public Integer getUserId(){
        String userId = request.getHeader("userId");
        if(Strings.isNullOrEmpty(userId)){
            return null;
        }else {
            return Integer.parseInt(userId);
        }
    }

    /**
     * 返回用户名称
     * @return
     */
    public String getUserName(){
        return request.getHeader("userName");
    }

    /**
     * 返回登录类型
     * @return
     */
    public Integer getLoginType(){
        String loginType = request.getHeader("loginType");
        if(Strings.isNullOrEmpty(loginType)){
            return null;
        }else {
            return Integer.parseInt(loginType);
        }
    }
}
