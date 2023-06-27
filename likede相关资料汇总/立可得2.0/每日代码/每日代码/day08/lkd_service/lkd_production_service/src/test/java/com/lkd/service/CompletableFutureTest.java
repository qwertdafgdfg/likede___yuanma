package com.lkd.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CompletableFutureTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        var aFuture = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return 1;
        });
        var bFuture = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        });
        var cFuture = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        });
        //并行处理
        CompletableFuture
                .allOf(aFuture,
                        bFuture,
                        cFuture)
                .join();
        //取值
        var a= aFuture.get();
        var b= bFuture.get();
        var c= cFuture.get();
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);

    }


}
