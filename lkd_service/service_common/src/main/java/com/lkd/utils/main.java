package com.lkd.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: liYuan
 * @Title: main
 * @ProjectName: lkd_parent
 * @Description:          //​ 可以使用此思路 完成数据批量存储 以及大集合的处理
 * @date: 2022/8/9 7:55
 */
public class main {

//​ 可以使用此思路 完成数据批量存储 以及大集合的处理
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();

            List<NameZ> list = createList();
            int i = 20;
            List<List<NameZ>> lists = SegmentationCollectionUtils.averageAssignList(list,i);
            System.out.println("主线程开始");
            //创建线程池子
            ExecutorService executorService = Executors.newCachedThreadPool();
            //线程同步围栏
            CountDownLatch cdl = new CountDownLatch(i);
            long end = System.currentTimeMillis();
            System.out.println((end - start)/1000 + "秒");
            long startList = System.currentTimeMillis();

            //线程池中取线程 执行集合
            //集合中有多少个任务就取多少个线程。。。
            lists.forEach((a)->
                    executorService.submit(() -> {
                                dealWith(a);
                                cdl.countDown();
                            }
                    ));

//            executorService.execute(() -> {
//                //这是一个线程任务被执行了。
//            });

            //主线程等待子线程执行完成再继续执行
            cdl.await();
            //关闭线程池
            executorService.shutdown();
            System.out.println("主线程结束");
            long endList = System.currentTimeMillis();
            System.out.println((endList - startList) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  List<NameZ> createList() {
        List<NameZ> strings = new ArrayList<>();
        for (int i=0;i<=10000000;i++){
            String s = UUID.randomUUID().toString().replaceAll("-", "").replaceAll(" ", "");
            strings.add(new NameZ(i, s,"app"));
        }
        return strings;
    }

    private static  void dealWith(List<NameZ> list){
        for (NameZ nameZ : list) {
            nameZ.setApp("bpp");
        }
        System.out.println(list.get(0));
    }
}
