package com.chenhaiyang.tcc.transaction.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * TCC 分布式事务上下文
 * @author chenhaiyang
 */
@AllArgsConstructor
@Data
public class TransactionContext implements Serializable {
    /**
     * 事务Xid
     */
    private TransactionXid xid;
    /**
     * 事务状态
     */
    private TransactionStatus status;
}
