package com.chenhaiyang.tcc.transaction.api;

/**
 * 获取事务全局相关属性
 * @author chenhaiyang
 */
public interface TransactionConfiguration {
    /**
     * 获取事务管理器
     * @return 返回结果 TransactionManager
     */
    TransactionManager getTransactionManager();

    /**
     * 获取事务存储介质
     * @return TransactionStorage
     */
    TransactionStorage getTransactionStorage();

    /**
     * 获取事务恢复配置
     * @return RecoverConfig
     */
    RecoverConfig getRecoverConfig();

    /**
     * 获取事务异步执行器
     * @return 返回结果
     */
    TransactionAysncExecutor getTransactionAysncExecutor();

    /**
     * 获取事务的业务域
     * @return 返回结果
     */
    String getDmoain();

}
