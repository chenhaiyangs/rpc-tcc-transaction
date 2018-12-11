package com.chenhaiyang.tcc.transaction.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 事务状态
 * @author chenhaiyang
 */
@AllArgsConstructor
public enum TransactionStatus implements Serializable{

    /**
     * 尝试中:1
     */
    TRYING(1),

    /**
     * 确认中:2
     */
    CONFIRMING(2),

    /**
     * 取消中:3
     */
    CANCELLING(3);

    /**
     * 状态Id
     */
    @Getter
    private int id;

}
