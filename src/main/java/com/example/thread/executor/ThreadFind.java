package com.example.thread.executor;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * todo
 * arraylist中有两万个，0-9的int型数据，怎么快速删除为2的
 * 多线程每个线程做一部分，并把结果合并起来（怎么实现）
 * 为什么没有 list.removeIf(next -> next == 2); 快?
 */
public class ThreadFind {

    private List<Integer> list = new ArrayList<>();

    //初始化测试数据
    @Before
    public void init() {
        for (int i = 0; i < 2000000; i++) {
            list.add(RandomUtils.nextInt(0, 10));
        }
    }

    @Test
    public void singleThread() {
        //37804  200万个耗时  41031
        long startTime = System.currentTimeMillis();
        list.removeIf(next -> next == 2);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }

    @Test
    public void thread4() throws InterruptedException, ExecutionException {
        //34937 没有想象中的效率提高?  40448
        //首先把list切分成小块,多线程每个线程做删除,最后在合并结果
        List<Integer> listTrue = new ArrayList<>(2000000);
        List<List<Integer>> lists = splitList(list, 500000);
        long startTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (List<Integer> list : lists) {
            Future<List> submit = threadPool.submit(new RemoveIntger2Thread(list));
//          listTrue.addAll(listTemp);
        }

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        System.out.println();
    }

    private List<Integer> threadFenDan(List<Integer> list, ExecutorService threadPool) throws InterruptedException, ExecutionException {
        Future<List> submit = threadPool.submit(new RemoveIntger2Thread(list));
        List list1 = submit.get();
        return list1;
    }

    //---------------------------------------把list平均成多少份---------
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n; //(先计算出余数)
        int number = source.size() / n; //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    private List<List<Integer>> splitList(List<Integer> list, int groupSize) {
        return Lists.partition(list, groupSize); // 使用guava
    }
    //---------------------------------------把list平均成多少份---------

    class RemoveIntger2Thread implements Callable<List> {

        private List<Integer> list;

        public RemoveIntger2Thread(List<Integer> list) {
            this.list = list;
        }

        public List<Integer> getList() {
            return list;
        }

        @Override
        public List call() throws Exception {
//        while (iterator.hasNext()) {
//            Integer next = iterator.next();
//            if (next == 2) {
//                iterator.remove();
//            }
//        }
            list.removeIf(next -> next == 2);
            return list;
        }
    }

}
