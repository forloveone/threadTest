package com.example.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 计算 1+2+3+4
 */
public class ForkJoinCountTask extends RecursiveTask<Integer> {
    private int start;
    private int end;

    public ForkJoinCountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    //临界值
    private static final int THRESHOLD = 2;

    /**
     * 首先要判断任务是否足够小,如果够小就直接执行任务,否则分割任务,
     */
    @Override
    protected Integer compute() {
        int sum = 0;
        //如果任务足够小就计算任务
        boolean canCompute = (end - start) <= THRESHOLD;
        if (canCompute) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            int middle = (start + end) / 2;
            ForkJoinCountTask t1 = new ForkJoinCountTask(start, middle);
            ForkJoinCountTask t2 = new ForkJoinCountTask(middle + 1, end);
            //执行子任务
            t1.fork();
            t2.fork();
            int t1Result = t1.join();
            int t2Result = t2.join();
            sum = t1Result + t2Result;
//            throw new RuntimeException("123");
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinCountTask task = new ForkJoinCountTask(1, 4);
        //判断出现异常的情况,好像用法不对
//        if (task.isCompletedAbnormally()) {
//            System.out.println(task.getException());
//        }
        ForkJoinTask<Integer> result = pool.submit(task);
        System.out.println(result.get());
    }
}
