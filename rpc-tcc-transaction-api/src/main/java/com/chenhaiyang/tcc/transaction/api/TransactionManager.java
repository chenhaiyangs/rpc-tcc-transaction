package com.chenhaiyang.tcc.transaction.api;

import com.chenhaiyang.tcc.transaction.api.exception.TransactionNotExistException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;

/**
 * 获取全局事务管理器
 * @author chenhaiyang
 */
public interface TransactionManager {
    /**
     * 启动一个tcc根事务
     */
    void begin();

    /**
     * 启动一个tcc事务分支事务上下文
     * @param transactionContext 事务上下文
     */
    void begin(TransactionContext transactionContext);

    /**
     * 改变当前事务的状态
     * @param transactionContext 返回结果
     * @throws TransactionNotExistException 事务不存在异常
     */
    void changeTransactionStatus(TransactionContext transactionContext) throws TransactionNotExistException;
    /**
     * 获取当前事务
     * @return 返回当前事务
     */
    Transaction getCurrentTransaction();
    /**
     * 对所有参与者进行事务提交
     * @param aysnc 是否异步执行
     */
    void commit(boolean aysnc);

    /**
     * 对所有的参与者进行事务回滚
     * @param aysnc 是否异步执行
     */
    void rollback(boolean aysnc);
}
