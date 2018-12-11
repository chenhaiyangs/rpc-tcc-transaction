package com.chenhaiyang.tcc.transaction.core.storage;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;

/**
 * 事务存储抽象类
 * @author chenhaiyang
 */
public abstract class AbstractStorage implements TransactionStorage{

    protected static final int SUCCESS=1;
    protected static final int FAIL=0;
    /**
     * 事务序列化实现
     */
    protected TransactionSerializer transactionSerializer;

    protected AbstractStorage(TransactionSerializer transactionSerializer) {
        this.transactionSerializer = transactionSerializer;
    }
}
