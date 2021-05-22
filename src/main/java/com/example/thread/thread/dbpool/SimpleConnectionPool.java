package com.example.thread.thread.dbpool;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * 摘自 java并发编程基础 一书
 */
public class SimpleConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public SimpleConnectionPool(int size) {
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    /**
     * 释放数据库连接
     *
     * @param connection
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

    //在时间内无法获取连接会返回null
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            if (mills <= 0) {
                //完全超时
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
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
