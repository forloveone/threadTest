package com.example.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个简单的阻塞队列 更复杂的可以看jdk实现
 */
public class SimpleBlockQueueTest {

    static class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition full = lock.newCondition();
        final Condition empty = lock.newCondition();

        final Object[] items = new Object[10];
        int putptr, takeptr, count;

        public void put(Object x) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) {
                    full.await();
                }
                items[putptr] = x;
                if (++putptr == items.length) {
                    putptr = 0;
                }
                ++count;
                empty.signal();
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    empty.await();
                }
                Object x = items[takeptr];
                if (++takeptr == items.length) takeptr = 0;
                --count;
                full.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }

    static class MyThread extends Thread {
        private BoundedBuffer list;

        public MyThread(BoundedBuffer list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println(list.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer list = new BoundedBuffer();
        //get
        MyThread thread = new MyThread(list);
        thread.start();
        //put
        for (int i = 0; i < 100; i++) {
            list.put(i);
        }
    }
}