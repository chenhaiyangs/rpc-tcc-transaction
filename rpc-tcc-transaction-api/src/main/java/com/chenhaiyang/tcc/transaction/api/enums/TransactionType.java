package com.chenhaiyang.tcc.transaction.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事务类型，标识其为一个主事务还是分支事务
 * @author chenhaiyang
 */
@AllArgsConstructor
public enum TransactionType {

    /**
     * 主事务:1 事务发起方
     */
    ROOT(1),

    /**
     * 分支事务:2 主事务负责调用分支事务
     */
    BRANCH(2);

    @Getter
    private int id;


}
