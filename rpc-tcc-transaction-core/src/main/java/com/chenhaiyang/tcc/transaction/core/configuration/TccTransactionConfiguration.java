package com.chenhaiyang.tcc.transaction.core.configuration;

import com.chenhaiyang.tcc.transaction.api.*;
import com.chenhaiyang.tcc.transaction.core.manager.DefaultTransactionManager;
import com.chenhaiyang.tcc.transaction.core.recover.DefaultTransactionRecoverConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * TCC事务配置器，通过事务配置器获取全部事务管理器
 * @author chenhaiyang
 */
@Slf4j
public class TccTransactionConfiguration implements TransactionConfiguration{
    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;
    /**
     * 事务存储器
     */
    private TransactionStorage transactionStorage;
    /**
     * 事务恢复配置
     */
    @Setter
    private RecoverConfig recoverConfig =new DefaultTransactionRecoverConfig();
    /**
     * 事务异步执行器
     */
    @Setter
    private TransactionAysncExecutor transactionAysncExecutor;
    /**
     * 事务的业务域
     */
    @Setter
    private String domain;

    public TccTransactionConfiguration() {
        this.transactionManager = new DefaultTransactionManager(this);
    }


    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * 设置事务存储器的时候调用init方法，比如自动执行初始化脚本
     * @param transactionStorage 事务存储器
     */
    public void setTransactionStorage(TransactionStorage transactionStorage) {
        this.transactionStorage = transactionStorage;
        try{
            int result = this.transactionStorage.init();
            log.info("create repostory result:{}",result);
        }catch (Exception e){
            log.warn("storage {} call init() fail ,cause:{}",transactionStorage,e);
        }
    }

    @Override
    public TransactionStorage getTransactionStorage() {
        return transactionStorage;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }

    @Override
    public TransactionAysncExecutor getTransactionAysncExecutor() {
        return this.transactionAysncExecutor;
    }

    @Override
    public String getDmoain() {
        return this.domain;
    }
}
