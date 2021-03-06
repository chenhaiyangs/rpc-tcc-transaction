package com.chenhaiyang.tcc.transaction.core.aysnc;

import com.chenhaiyang.tcc.transaction.api.TransactionAysncExecutor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池提交者
 * @author chenhaiyang
 */
@NoArgsConstructor
@Slf4j
public class ThreadPoolTransactionAysncExecutor implements TransactionAysncExecutor{

    private ThreadPoolExecutor pools = null;

    /**
     * 用于异步处理事务提交或者回滚的线程
     * @param corePoolSize 最小线程数
     * @param maxPoolSize 最大线程数
     * @param keepAliveTime 超时回收时间，单位秒
     */
    public ThreadPoolTransactionAysncExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        this.pools =new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new SynchronousQueue<>(),new TransactionThreadFactory());
    }

    /**
     * 提交任务
     * @param task task
     */
    @Override
    public void submit(Runnable task){
        if(pools!=null) {
            pools.submit(task);
        }else {
            //并没有配置线程池，因此无法异步执行，但是如果调用，则实际上在当前线程执行，这里打印一下警告
            log.warn("threadPoolExecutor is null, so execuse not by runnable!");
            task.run();
        }
    }

    /**
     * 内部线程池工厂
     */
    private static class TransactionThreadFactory implements ThreadFactory{
        /**
         * 线程编号
         */
        private static final AtomicLong THREAD_NUMBER = new AtomicLong(1);
        /**
         * 线程组
         */
        private static final ThreadGroup THREAD_GROUP = new ThreadGroup("tccTransaction");

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(THREAD_GROUP, runnable,
                    THREAD_GROUP.getName() + "-" + "commit_cancel_thread" + "-" + THREAD_NUMBER.getAndIncrement());
        }
    }

}
