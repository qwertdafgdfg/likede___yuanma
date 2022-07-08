package com.lkd.utils;

/**
 * 用户角色判断工具类
 */
public class UserRoleUtils {
    /**
     * 是否是运维人员
     * @param roleId
     * @return
     */
    public static Boolean isRepair(int roleId){
        return roleId == 3;
    }

    /**
     * 是否是运营员
     * @param roleId
     * @return
     */
    public static Boolean isOperator(int roleId){
        return roleId == 2;
    }
}
