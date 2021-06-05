package com.example.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞队列 test
 * todo 常用阻塞队列
 */
public class BlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue queue = new ArrayBlockingQueue(0);
        queue.put(1);

        Object take = queue.take();
        System.out.println(take);

        boolean offer = queue.offer(2, 1, TimeUnit.MINUTES);
        Object poll = queue.poll(1, TimeUnit.MINUTES);
        System.out.println(poll);

        Object poll1 = queue.poll();
        System.out.println(poll1);

        boolean remove = queue.remove(2);
        System.out.println(remove);
    }
}
