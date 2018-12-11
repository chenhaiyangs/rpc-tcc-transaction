package com.chenhaiyang.tcc.transaction.dashboard.page;

import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import lombok.Data;

/**
 * 事务操作vo
 * @author chenhaiyang
 */
@Data
public class OperateVo {
    /**
     * 事务对象
     */
    private Transaction transaction;
    /**
     * 事务的业务域
     */
    private String domain;
    /**
     * 事务的全局事务id
     */
    private String globalTransactionId;
    /**
     * 事务的分支限定id
     */
    private String branchQualifier;
    /**
     * 事务Id
     * @param transaction 事务
     * @return 返回包装类
     */
    public static OperateVo cast(Transaction transaction) {
        OperateVo operateVo = new OperateVo();
        operateVo.setTransaction(transaction);
        operateVo.setDomain(transaction.getDomain());
        operateVo.setBranchQualifier(new String(transaction.getXid().getBranchQualifier()));
        operateVo.setGlobalTransactionId(new String(transaction.getXid().getGlobalTransactionId()));
        return operateVo;
    }

    /**
     * 转transaction
     * @param operateVo vo
     * @return transaction
     */
    public static Transaction toTransaction(OperateVo operateVo) {
        TransactionXid xid = new TransactionXid(operateVo.globalTransactionId.getBytes(),operateVo.branchQualifier.getBytes());
        Transaction transaction = new Transaction(xid, TransactionStatus.TRYING, TransactionType.ROOT);
        transaction.setDomain(operateVo.getDomain());
        return transaction;
    }
}
