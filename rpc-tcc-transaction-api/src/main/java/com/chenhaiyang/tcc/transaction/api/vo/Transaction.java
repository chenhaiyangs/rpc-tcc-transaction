package com.chenhaiyang.tcc.transaction.api.vo;

import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 事务实体域
 * @author chenhaiyang
 */
@Data
public class Transaction implements Serializable,Cloneable{
    /**
     * 事务所属业务域
     */
    private String domain;
    /**
     * 事务XId
     */
    private TransactionXid xid;

    /**
     * 事务状态.
     */
    private TransactionStatus status;

    /**
     * 事务类型 (1,主事务，2，分支事务 )
     */
    private TransactionType transactionType;

    /**
     * 事务恢复重试次数
     */
    private volatile int retriesCount = 0;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime = new Date();
    /**
     * 事务下次处理的时间（事务恢复时查该时间）
     */
    private Date nextProcessTime=new Date();

    /**
     * 版本（默认值为1）
     */
    private long version = 1;

    /**
     * 参与者列表.（参与者列表就是一个commit方法和一个cancel方法）
     */
    private List<Participant> participants = new ArrayList<>();


    public Transaction(TransactionXid transactionXid, TransactionStatus status, TransactionType type) {
        this.xid=transactionXid;
        this.status=status;
        this.transactionType=type;
    }

    /**
     * 添加事务参与者
     * @param participant 事务参与者
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    /**
     * 事务提交
     */
    public void commit() {
        for (Participant participant : participants) {
            participant.commit();
        }
    }

    /**
     * 事务回滚
     */
    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    public void updateVersion() {
        this.version++;
    }

    public void updateLastUpdateTime() {
        this.lastUpdateTime=new Date();
    }

    @SuppressWarnings("all")
    @Override
    public Transaction clone(){
        Transaction transaction = new Transaction(this.xid,this.status,this.transactionType);
        transaction.setRetriesCount(this.retriesCount);
        transaction.setCreateTime(this.createTime);
        transaction.setLastUpdateTime(this.lastUpdateTime);
        transaction.setNextProcessTime(this.nextProcessTime);
        transaction.setParticipants(this.participants);
        transaction.setVersion(this.version);
        transaction.setDomain(this.domain);
        return transaction;
    }
}
