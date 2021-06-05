package com.example.thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Executors 框架 测试
 */
public class ExecutorsTest {

    public static void main(String[] args) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("hello   " + i);
                }
            }
        };
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("hello2  " + i);
            }
        });
        //最多只有一个线程可以活动,如果多个提交会等到线程可用为止
//        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.submit(t1);
        threadPool.submit(t2);
        threadPool.shutdown();
    }
}
