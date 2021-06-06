package com.example.thread;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 锁的应用
 * 代理模式
 * 摘自 java并发编程基础 一书
 */
public class SimpleConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    static class ConnectionDriver {
        static class ConnectionHandler implements InvocationHandler {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("commit")) {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                return null;
            }
        }

        //创建一个Connection的代理,在commit时休眠100毫秒.
        public static final Connection createConnection() {
            return (Connection) Proxy.newProxyInstance(
                    ConnectionDriver.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    new ConnectionHandler());
        }
    }

    /**
     * 初始化 线程池
     * @param size 大小
     */
    public SimpleConnectionPool(int size) {
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    /**
     * 释放数据库连接
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                //把连接还回到 pool
                pool.addLast(connection);
                //释放连接后需要进行通知,这样其他消费者能够感知到连接池中已经归还了一个连接.
                pool.notifyAll();
            }
        }
    }

    /**
     * 在时间内无法获取连接会返回null
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            if (mills <= 0) {
                //完全超时
                while (pool.isEmpty()) {
                    //等待数据库连接归还 pool.notifyAll() ,poll is not empty
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    //计算剩余时间
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty() && remaining > 0) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}