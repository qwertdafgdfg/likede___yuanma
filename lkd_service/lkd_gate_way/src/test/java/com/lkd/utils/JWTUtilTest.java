package com.lkd.utils;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

public class JWTUtilTest{

    @Test
    public void decode() throws IOException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJsa2QiLCJpYXQiOjE1NzE1MzM3MTIsInVzZXJOYW1lIjoi566h55CG5ZGYIiwidXNlcklkIjoxLCJyb2xlQ29kZSI6IjEwMDAiLCJjb21wYW55SWQiOjAsImV4cCI6MTU3MTUzMzcxMn0.scvbx6q96KPV_mXjs3EdQNSkOK-GR1ivu7RKRUnL0B4";
//        TokenObject tokenObject = JWTUtil.decode(token);


    }

    @Test
    public void generate(){
        for (int i = 0; i < 20; i++) {
            StringBuilder sbCode = new StringBuilder();
            Stream
                    .generate(()-> new Random().nextInt(10))
                    .limit(5)
                    .forEach(x-> sbCode.append(x));
            System.out.println(sbCode.toString());
            sbCode.setLength(0);
        }
    }

    @Test
    public void base64decode() throws IOException {
        byte[] encodedKey = java.util.Base64.getDecoder().decode("aaaaaaaaaaab");
        //BaseEncoding.base64().decode("jwtSecret");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJsa2QiLCJpYXQiOjE2MDY4ODc4NDAsIm1vYmlsZSI6IjEzODAwMDAwMDAwIiwidXNlcklkIjoxLCJsb2dpblR5cGUiOjEsImV4cCI6MTYwNjg4Nzg0MH0.c2_JUfZpqF-iQMXBu2mdyi5IoC3bnESeSHB63o2BZXw";
        JWTUtil.decode(token);
        //DatatypeConverter.
        System.out.println(encodedKey);
    }
}