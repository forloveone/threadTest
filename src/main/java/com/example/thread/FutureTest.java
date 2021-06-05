package com.example.thread;

import java.util.concurrent.*;

/**
 * 任务的生命周期只能前进不能后退
 */
public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        TaskForFuture task = new TaskForFuture();
        Future<String> future = new FutureTask<String>(task);
        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<String> submit = service.submit(task);
        String o = null;
        try {
            o = submit.get(1, TimeUnit.SECONDS);//这个会跳到TimeoutException中
//            o = submit.get();//会一直阻断到任务结束为止
        } catch (InterruptedException e) {
            e.printStackTrace();
            //被中断,也就是被其他线程通知要结束.
        } catch (ExecutionException e) {
            e.printStackTrace();
            //记录日志,在任务中抛出了异常,那么继续抛出该异常
        } catch (TimeoutException e) {
//            e.printStackTrace();
            //记录日志并 取消任务
            System.out.println("任务被取消");
            submit.cancel(true);
        }
        System.out.println(o);

        service.shutdown();//和shutdownnow的区别 TODO
    }


    static class TaskForFuture implements Callable<String> {
        @Override
        public String call() throws Exception {
//                Thread.sleep(10000);
            for (int i = 0; i < 1000000; i++) {
                System.out.println(i);
            }
            return "abc";
        }
    }
}