
# thread test
## this project is my thread test

两本书
vnote笔记
# 原理
    并发就是利用cpu多核心的关键技术
    
我们讲的并发其实是并行和并发一起的
    并发
        是同时应对多件事情的能力
    并行
        是同时做多件事的能力, 主要是任务级别并行,其依赖的基础是计算机的多处理器架构.到了微观层面由于cpu数目的限制仍然是串行或者部分串行.
        如果只有一个CPU，CPU在某一个时刻只能执行一条指令，线程只有得到CPU时间片，也就是使用权，才可以执行指令。在单CPU的机器上线程不是并行运行的，只有在多个CPU上线程才可以并行运行
## 内存模型
    java所有线程共享主内存，但是每一个线程都有自己的工作内存是，线程只能访问自己的工作内存，因此在多线程环境下，很容易导致工作内存不一致而引起并发问题。
    可以类比为主内存是堆，工作内存是栈
## 重排序
    编译器和处理器的重排序没有考虑多线程，多处理器的情况，在并发环境会造成不可知的问题。
    在执行程序时，为了提高性能，编译器和处理器会对指令重排序。
        编译器优化
            在不改变单线程语义的情况下，可以重排序语句执行顺序
        指令级重排序
            如果不存在数据依赖，处理器可以改变语句对应机器指令的执行顺序。
## 可变数据

# 并发正确性即线程安全
    内存模型，重排序，可变数据是线程不安全的根本原因
    
    处理方式都是围绕如何处理
        原子性
            对于对象的操作，要么成功要么失败，没有中间状态
            synchronized
        可见性
            happens before
            final
            volatile
            synchronized
        有序性
            volatile
            synchronized    
    来进行的
# 线程同步即线程间通信
    阻塞队列
# 并发工具
    reentrantLock
    
    CAS机制
    
    ThreadLocal
    
    不可变对象
    
    并发集合
        concurrentHashMap
        CopyOnWriteArrayList
        LinkedBlockingQueue
    
    同步器
        CountDownLatch
        CyclicBarrier
        Semaphore
# 并发框架
    executor
    fork/join

# 建议
    给线程起名，帮助调试
    尽量使用final
    总是按照一个全局的固定顺序来获取多把锁，可以避免死锁
    最小化同步的范围
    分段锁
    使用高层次并发工具
    高并发慎用框架




























