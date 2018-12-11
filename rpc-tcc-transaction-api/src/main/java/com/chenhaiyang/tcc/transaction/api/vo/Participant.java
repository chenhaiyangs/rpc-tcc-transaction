package com.chenhaiyang.tcc.transaction.api.vo;

import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 事务参与者。参与者就是一个confirm和cancel方法
 * @author chenhaiyang
 */
@AllArgsConstructor
@Data
public class Participant implements Serializable {
    /**
     * 事务Xid
     */
    private TransactionXid xid;
    /**
     * 确认调用的上下文.
     */
    private InvocationContext confirmInvocationContext;

    /**
     * 取消调用的上下文.
     */
    private InvocationContext cancelInvocationContext;

    /**
     * 回滚参与者事务（在Transaction中被调用）
     */
    void rollback() {
        cancelInvocationContext.invoke();
    }

    /**
     * 提交参与者事务（在Transaction中被调用）.
     */
    void commit() {
        confirmInvocationContext.invoke();
    }
}
