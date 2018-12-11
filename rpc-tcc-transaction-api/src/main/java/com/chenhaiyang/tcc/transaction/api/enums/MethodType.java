package com.chenhaiyang.tcc.transaction.api.enums;

/**
 * 方法类型
 * @author chenhaiyang
 */
public enum  MethodType {
    /**
     * 根事务方法(表示一个主事务的发起者）
     */
    ROOT,

    /**
     * 消费者（表示一个主事务的登记其分支事务的接口方法）
     */
    CONSUMER,

    /**
     * 提供者 （表示登记的分支事务的具体实现类，用于发起分支事务的具体实现）
     */
    PROVIDER,
    /**
     * 不是一个具体事务方法
     */
    NONE
}
