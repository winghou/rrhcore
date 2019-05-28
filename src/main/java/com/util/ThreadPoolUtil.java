package com.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 一、LinkedBlockingQueue，除非系统资源耗尽，否则不存在任务队列入队失败的情况。
 * 二、当有新任务需要执行时，如果线程池的实际线程数小于corePoolSize，则会创建新线程执行任务；但当池中线程数达到corePoolSize后，就不会再创建新的线程！若后续有新任务需要执行，且没有空闲的线程时，则任务进入队列等待。
 * 三、如果任务创建和处理速度相差很大，LinkedBlockingQueue会快速增长，直到耗尽系统内存
 * 线程池工厂类<br>
 * 〈功能详细描述〉
 */
public class ThreadPoolUtil {

    private static ThreadPoolUtil instance = new ThreadPoolUtil();

    private ThreadPoolUtil() {
        int cpuNums = Runtime.getRuntime().availableProcessors();
        threadPool = new ThreadPoolExecutor(cpuNums * 2, 50, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public ThreadPoolExecutor threadPool;

    public static void exec(Runnable runnable) {
        instance.threadPool.execute(runnable);
    }

    public static <T> Future<T> sub(Callable<T> runnable) {
        return instance.threadPool.submit(runnable);
    }
}
