//package com.lkd.utils;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.lkd.http.view.TokenObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//
//import static org.junit.Assert.*;
//
//
//public class JWTUtilTest{
//
//    @Test
//    public void createJWTByObj() throws IOException {
//        TokenObject tokenObject = new TokenObject();
//        tokenObject.setCompanyId(1);
//        tokenObject.setRoleCode("1000");
//        tokenObject.setUserId(1);
//        tokenObject.setUserName("测试账号");
//
//        String token = JWTUtil.createJWTByObj(tokenObject,"ttsada");
//
//        int a = 0;
//    }
//
//    @Test
//    public void verify(){
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJsa2QiLCJpYXQiOjE1Njk5MTQzNDgsImV4cGlyZXMiOm51bGwsInVzZXJOYW1lIjoi5rWL6K-V6LSm5Y-3IiwidXNlcklkIjoxLCJyb2xlQ29kZSI6IjEwMDAiLCJjb21wYW55SWQiOjEsImV4cCI6MTU2OTkxNDM0OH0.OUx6CPZ3niITZdw3xmuVnxT74tA57NZyz7vui94dEyo";
//        JWTUtil.verifyJwt(token,"ttsada");
//
//        int a = 0;
//    }
//}
