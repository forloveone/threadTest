package com.example.thread.thread;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {
    /**
     * sleep join
     */
    @Test
    public void test() throws InterruptedException {
        //这个是继承Thread类的方式创建线程
        Thread temp = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    System.out.println("pong");
                    //这是一个静态方法，作用是让当前线程“让步”，目的是为了让优先级不低于当前线程的线程有机会运行，这个方法不会释放锁。
                    //屈服 但是这个例子好像没有效果
                    yield();
                    interrupt();
                }
            }
        };

        //获取当前线程对象
        String name = Thread.currentThread().getName();
        System.out.println(name);

        Thread thread = Thread.currentThread();
        //在那个线程中调用了线程的sleep(),那个线程就会睡觉
        thread.sleep(10000);
        System.out.println("test");

        temp.start();
        //等待temp线程完成,如果有时间的话,超过时间会继续往下执行
        //Waits for this thread to die.
        temp.join(50000);
        System.out.println("join test");
    }

    //不能使用Test测试,没有看出效果,用main方法测试
    //当JVM发现只有守护线程在运行的时候,JVM会自动的关闭守护线程,然后关闭JVM
    //    @Test
    public void daemoThread() {
        // public static void main(String[] args) {
        class DaemoThread extends Thread {
            public DaemoThread(String deamothrea) {
                super(deamothrea);
                //设置为守护线程
                this.setDaemon(true);
                start();
            }

            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    System.out.println("daemo Thread" + i);
                }
            }
        }

        DaemoThread daemoThread = new DaemoThread("deamothrea");
        String name = daemoThread.getName();
        System.out.println(name);
        System.out.println("1");
    }

    @Test
    public void runAble() {
        class TempThread implements Runnable {
            @Override
            public void run() {
                System.out.println("test");
            }
        }
        TempThread t = new TempThread();
        Thread t2 = new Thread(t, "tempName");
        t2.start();
        System.out.println(t2.getName());
    }

    private int a;

    @Test
    public void test2() throws InterruptedException {
        Thread t = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000000; i++) {
                    a++;
                    //线程通过isInterrupted 判断是否被中断,如果被中断可以处理其他逻辑
                    //可以用来取消或停止任务(能够使线程在终止时有机会去清理资源,而不是武断的停止线程)
                    if (this.isInterrupted()) {
                        System.out.println("test interrupt");
                        return;
                    }
                }
            }
        };
        t.start();
        t.interrupt();
//        System.out.println(t.isInterrupted());
//        System.out.println(t.isAlive());
        //如果主线程 直接去a 为 0
        Thread.sleep(10);
        System.out.println(a);
    }

    @Test
    public void call() throws Exception {
        class TempTest implements Callable<String> {

            @Override
            public String call() throws Exception {
                System.out.println("call run");
                Thread.sleep(10000);
                return "secsess";
            }
        }
        TempTest temp = new TempTest();
        String call = temp.call();
        Thread.sleep(10000);
        System.out.println(call);

    }

    /**
     * ThreadLocal<Map> local = new ThreadLocal<>();
     * 对象中的ThreadLocal,可以为每一个线程分配自己的本地存储
     */
    @Test
    public void threadLocal() throws InterruptedException {
        ThreadPojo test = new ThreadPojo();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                Map map = new HashMap<>();
                map.put("1", "123");
                test.getThreadPrivateMap().set(map);
                Map to = test.getThreadPrivateMap().get();
                System.out.println(to.get("1"));
            }
        };
        t1.start();
        Thread.sleep(5000);
        Map mainMap = test.getThreadPrivateMap().get();
        mainMap.get("1");
    }

    /**
     * 判断某个线程是否持有对象监视器(就是锁)
     */
    //    @Test
    public void lock() {
//    public static void main(String[] args) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    testLock(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
    }

    public static Object obj = new Object();
    public static Object obj2 = new Object();

    public static void testLock(Thread thread) throws InterruptedException {
        synchronized (obj) {
            System.out.println("业务逻辑");
            System.out.println("持有锁判定" + thread.holdsLock(obj));
        }
    }

    /**
     * 死锁简单示例
     * 两个线程里面分别持有两个Object对象 ,并请求对方的对象
     * 这个例子线程1一直没有释放锁,并请求锁2,线程2持有锁2并请求锁1,造成死锁
     * 线程 dump 信息
     * "Thread B" #13 prio=5 os_prio=0 tid=0x000000001a8b3800 nid=0xc60 waiting for monitor entry [0x000000001b46f000]
     * java.lang.Thread.State: BLOCKED (on object monitor)
     * at com.test.thread.ThreadTest.dead1(ThreadTest.java:330)
     * - waiting to lock <0x00000000d64c4428> (a java.lang.Object)
     * at com.test.thread.ThreadTest.dead2(ThreadTest.java:339)
     * - locked <0x00000000d64c4438> (a java.lang.Object)
     * at com.test.thread.ThreadTest$7.run(ThreadTest.java:318)
     * <p>
     * Locked ownable synchronizers:
     * - None
     * <p>
     * "Thread A" #12 prio=5 os_prio=0 tid=0x000000001a8b3000 nid=0xaec waiting for monitor entry [0x000000001b36f000]
     * java.lang.Thread.State: BLOCKED (on object monitor)
     * at com.test.thread.ThreadTest.dead2(ThreadTest.java:338)
     * - waiting to lock <0x00000000d64c4438> (a java.lang.Object)
     * at com.test.thread.ThreadTest.dead1(ThreadTest.java:332)
     * - locked <0x00000000d64c4428> (a java.lang.Object)
     * at com.test.thread.ThreadTest$6.run(ThreadTest.java:305)
     * <p>
     * Locked ownable synchronizers:
     * - None
     */
//    @Test
    public void deadLock() {
//    public static void main(String[] args) {

        Thread t1 = new Thread("Thread A") {
            @Override
            public void run() {
                try {
                    dead1(this.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();

        Thread t2 = new Thread("Thread B") {
            @Override
            public void run() {
                try {
                    dead2(this.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t2.start();
    }

    public static void dead1(String name) throws InterruptedException {
        synchronized (obj) {
            System.out.println(name + "获得第一个锁");
            //想持有第二个锁
            dead2(name);
        }
    }

    public static void dead2(String name) throws InterruptedException {
        synchronized (obj2) {
            System.out.println(name + "获得第二个锁");
            dead1(name);
        }
    }

    public void threadPoolSize() {
//    public static void main(String[] args) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("hello   " + i);
                }
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("hello2  " + i);
                }
            }
        };
        //最多只有一个线程可以活动,如果多个提交会等到线程可用为止
//        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.submit(t1);
        threadPool.submit(t2);
        threadPool.shutdown();
    }

    /**
     * 唤醒一个阻塞的线程
     * 如果线程是因为调用了wait()、sleep()或者join()方法而导致的 阻塞，可以中断线程，并且通过抛出InterruptedException来唤醒它
     * 如果线程遇到了IO阻塞，无能为力，因为IO是操作系统实现的
     */
//    public void weak() {
    public static void main(String[] args) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //sleep 不会放弃持有的锁
//                    sleep(100000);
                    //wait会放弃持有的锁
//                    wait(100000);
                    join(10000);
                    System.out.println("被唤醒");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("任务被中断");
                }
            }
        };
        t.start();
        //中断此线程
//        t.interrupt();
//        boolean interrupted = t.isInterrupted();
//        System.out.println(interrupted);
        for (int i = 0; i < 100; i++) {
            System.out.println("业务逻辑整理" + i);
        }
    }

    private static Lock lock = new ReentrantLock();
    private static volatile int value;

    /*
        完成了给reentrantLock方法“上锁”
        if (myLock.tryLock()) {
            try {
                …
            } finally {
                myLock.unlock();
            }
        } else {
            //做其他的工作
        }
     */
    //这个方法等价于给方法加 synchronized
    public static void reentrantLock(String name) {
        lock.lock();
        try {
            value++;
            System.out.println(name + "  " + value);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void add() throws InterruptedException {
        while (value <= 5) {
            //wait方法添加一个线程到这个条件的等待集中；
            //notifyAll / notify方法会唤醒等待集中的线程
            wait();
        }
        value++;
        notifyAll();
    }

    //    public static void main(String[] args) throws InterruptedException {
    public void reentrantLock() throws InterruptedException {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    reentrantLock(this.getName());
                }
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    reentrantLock(this.getName());
                }
            }
        };
        t2.start();
        t1.join();
        t2.join();
        System.out.println(value);
    }

    @Test
    public void thisOutSide() {

    }

    @Test
    public void threadCollection() throws InterruptedException {
        //hashMap的线程安全版本
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("1", "map1");
        map.put("2", "map1");
        map.put("3", "map1");
        map.put("4", "map1");

        CopyOnWriteArrayList list = new CopyOnWriteArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
    }

    private static boolean waitAndNotifyFlag = true;
    private static Object waitAndNotifyObj = new Object();
    private static WaitAndNotifyLogicObject waiObj = new WaitAndNotifyLogicObject();

    //一个线程修改了一个对象的值,而另一个线程感知到了变化,然后进行相应的操作.
    //页实现了超时 切换逻辑
    public void waitAndNotify() throws InterruptedException {
//    public static void main(String[] args) throws InterruptedException {
        Thread consumer = new Thread(new Consumer(), "Consumer");
        consumer.start();
        Thread.sleep(10000);
        Thread product = new Thread(new Product(), "Product");
        product.start();
    }

    //生产者
    static class Product implements Runnable {

        @Override
        public void run() {
            synchronized (waitAndNotifyObj) {
                //修改对象的值
                waiObj.age = 100;
                waitAndNotifyFlag = false;
                waitAndNotifyObj.notifyAll();
                System.out.println("生产者 生产了东西并把状态设置为true");
            }
        }
    }

    //消费者
    static class Consumer implements Runnable {

        @Override
        public void run() {
            long future = System.currentTimeMillis() + 5000L;
            long remaining = 5000;
            synchronized (waitAndNotifyObj) {
                while (waitAndNotifyFlag && remaining > 0) {
                    try {
                        waitAndNotifyObj.wait(remaining);
                        remaining = future - System.currentTimeMillis();
                        System.out.println(remaining + "这个变量是知道它是否超时的,所以上边哪行代码有意义");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (remaining <= 0) {
                    //超时
                    System.out.println("超时");
                } else {
                    System.out.println("消费者 执行主业务逻辑" + (waiObj.age + 1));
                }
            }
        }
    }

    static class WaitAndNotifyLogicObject {
        public String name;
        public int age;
    }


    private static final Exchanger<String> exchangerStr = new Exchanger<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(2);

    /**
     * Exchanger 用于进行线程间的数据交换.它提供一个同步点,在这个同步点,两个线程可以交换彼此的数据.
     * <p>
     * 如果一个线程想执行exchange() 它会一直等待第二个线程也执行exchange方法.
     */
//    public static void main(String[] args) {
    public void exchanger() {
        pool.submit(new Thread(new A()));
        pool.submit(new Thread(new B()));
        pool.shutdown();
    }

    static class A implements Runnable {

        @Override
        public void run() {
            String a = "tesaA";
            try {
                String exchange = exchangerStr.exchange(a);
                System.out.println("A交换到的数据" + exchange + " A的原数据" + a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class B implements Runnable {

        @Override
        public void run() {
            String b = "tesaB";
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                String exchange = exchangerStr.exchange(b);
                System.out.println("B交换到的数据" + exchange + " B的原数据" + b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
