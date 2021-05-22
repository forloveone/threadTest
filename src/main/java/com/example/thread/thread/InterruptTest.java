package com.example.thread.thread;

/**
 * Thread的interrrupt方法，本质不是用来中断一个线程。是将线程设置一个中断状态。
 * <p>
 * 当我们调用线程的interrupt方法，它有两个作用：
 * <p>
 * 1、如果此线程处于阻塞状态(比如调用了wait方法，io等待)，则会立马退出阻塞，并抛出InterruptedException异常，
 * 线程就可以通过捕获InterruptedException来做一定的处理，然后让线程退出。
 * <p>
 * 2、如果此线程正处于运行之中，则线程不受任何影响，继续运行，仅仅是线程的中断标记被设置为true。
 * 所以线程要在适当的位置通过调用isInterrupted方法来查看自己是否被中断，并做退出操作。
 */
public class InterruptTest {
    public static void main(String[] args) throws InterruptedException {
        ThreadTemp t = new ThreadTemp();
        ThreadTemp3 t3 = new ThreadTemp3();
        Thread thread = new Thread(t3);
        thread.start();
        //将线程设置一个中断状态
        Thread.sleep(10);
        thread.interrupt();
    }
}

class ThreadTemp implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            try {
                System.out.println(i);
                boolean interrupted = Thread.currentThread().isInterrupted();
                System.out.println(interrupted);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 会一直运行,并没有停止
 */
class ThreadTemp2 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            System.out.println(i);
            boolean interrupted = Thread.currentThread().isInterrupted();
            System.out.println(interrupted);
        }
    }
}

class ThreadTemp3 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            if (!Thread.currentThread().isInterrupted()) {
                System.out.println(i);
            }
        }
    }
}