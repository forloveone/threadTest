package com.example.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * volatile
 * 没有理解写的什么? todo 线程池,那个池的概念在哪里
 */
public class SimpleThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    //线程池最大数量
    private static final int MAX_SIZE = 10;
    //线程池默认数量
    private static final int DEFAULT_SIZE = 5;
    //线程池最小数量
    private static final int MIN_SIZE = 1;
    //工作列表
    private final LinkedList<Job> jobs = new LinkedList<>();

    //工作者列表
    private final List<Worker> workers = (List<Worker>) Collections.synchronizedCollection(new ArrayList<Worker>());

    private int workerNum = DEFAULT_SIZE;

    // 线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public SimpleThreadPool() {
        initWorkers(DEFAULT_SIZE);
    }

    public SimpleThreadPool(int num) {
        int size = num;
        if (num >= MAX_SIZE) {
            size = MAX_SIZE;
        }
        if (num <= MIN_SIZE) {
            size = MIN_SIZE;
        }
        initWorkers(size);
    }

    @Override
    public void execute(Job job) {
        if (job != null) {
            //添加一个工作,然后进行通知
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {}

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
//            if (num+this.workerNum)
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    private void initWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    //工作者负责消费任务
    class Worker implements Runnable {
        //是否工作标志
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    // 如果工作者列表是空的,那就wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对worker thread的中断操作,返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    job = jobs.removeFirst();

                    if (job != null) {
                        try {
                            job.run();
                        } catch (Exception e) {
                            //忽略异常.
                        }
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}

interface ThreadPool<Job extends Runnable> {
    //执行一个任务
    void execute(Job job);

    //关闭线程池
    void shutdown();

    //增加工作者线程
    void addWorkers(int num);

    //减少工作者线程
    void removeWorker(int num);

    //得到正在等待执行的任务数量
    int getJobSize();
}