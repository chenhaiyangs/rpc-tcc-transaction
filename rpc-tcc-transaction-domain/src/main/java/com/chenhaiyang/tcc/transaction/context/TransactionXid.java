package com.chenhaiyang.tcc.transaction.context;

import lombok.Data;

import javax.transaction.xa.Xid;
import java.io.Serializable;

/**
 * 全局事务服务
 * Xid： 指一个XA事务。不同的数据库要不同的 Xid（每个数据库连接（分支）一个）
 * 请查看：http://www.zgqxb.com.cn/mydoc/j2se_api_cn/javax/transaction/xa/Xid.html
 * @author chenhaiyang
 */
@Data
public class TransactionXid implements Xid,Serializable,Cloneable{

    /**
     * XID 的格式标识符,这里写死为1
     */
    private int formatId = 1;
    /**
     * 全局事务Id
     */
    private byte[] globalTransactionId;

    /**
     * 分支限定Id
     */
    private byte[] branchQualifier;

    /**
     * 新建一个transactionXid
     * @param globalTransactionId 全局事务Id
     * @param branchQualifier 分支限定Id
     */
    public TransactionXid(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    /**
     * 克隆事务Xid
     */
    @Override
    public TransactionXid clone() {

        try {
            return (TransactionXid)super.clone();
        } catch (CloneNotSupportedException e) {

            byte[] cloneGlobalTransactionId = new byte[globalTransactionId.length];
            byte[] cloneBranchQualifier = new byte[branchQualifier.length];

            System.arraycopy(globalTransactionId, 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
            System.arraycopy(branchQualifier, 0, cloneBranchQualifier, 0, branchQualifier.length);

            return new TransactionXid(cloneGlobalTransactionId, cloneBranchQualifier);
        }
    }

}
