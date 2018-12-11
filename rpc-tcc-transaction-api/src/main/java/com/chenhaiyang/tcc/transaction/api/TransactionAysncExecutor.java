package com.chenhaiyang.tcc.transaction.api;

/**
 * 事务异常执行器
 * @author chenhaiyang
 */
public interface TransactionAysncExecutor {
    /**
     * 提交任务
     * @param task task
     */
    void submit(Runnable task);
}
