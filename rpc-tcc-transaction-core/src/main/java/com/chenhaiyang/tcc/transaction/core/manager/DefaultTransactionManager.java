package com.chenhaiyang.tcc.transaction.core.manager;

import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.TransactionManager;
import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.exception.CancellingException;
import com.chenhaiyang.tcc.transaction.api.exception.ConfirmingException;
import com.chenhaiyang.tcc.transaction.api.exception.TransactionNotExistException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认的事务管理类
 * @author chenhaiyang
 */
@Slf4j
public class DefaultTransactionManager implements TransactionManager{
    /**
     * 定义当前线程的事务局部变量.
     */
    private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<>();
    /**
     * 事务存储器
     */
    private TransactionConfiguration transactionConfiguration;

    public DefaultTransactionManager(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration=transactionConfiguration;
    }

    @Override
    public Transaction getCurrentTransaction() {
        return threadLocalTransaction.get();
    }

    @Override
    public void begin() {
        TransactionXid xid = new TransactionXid(IdUtils.newId(),IdUtils.newId());
        Transaction transaction = new Transaction(xid,TransactionStatus.TRYING,TransactionType.ROOT);
        transaction.setDomain(transactionConfiguration.getDmoain());
        int result = transactionConfiguration.getTransactionStorage().create(transaction);
        log.info("create root transaction result:{}",result);
        threadLocalTransaction.set(transaction);

    }

    @Override
    public void begin(TransactionContext transactionContext) {
        TransactionXid transactionXid = transactionContext.getXid();
        TransactionStatus status = transactionContext.getStatus();
        Transaction transaction = new Transaction(transactionXid,status,TransactionType.BRANCH);
        transaction.setDomain(transactionConfiguration.getDmoain());
        int result = transactionConfiguration.getTransactionStorage().create(transaction);
        log.info("create branch transaction result:{}",result);
        threadLocalTransaction.set(transaction);
    }

    @Override
    public void changeTransactionStatus(TransactionContext transactionContext) throws TransactionNotExistException {

        TransactionXid transactionXid = transactionContext.getXid();
        Transaction transactionQuery = new Transaction(transactionXid,null,null);
        transactionQuery.setDomain(transactionConfiguration.getDmoain());

        Transaction transaction = transactionConfiguration.getTransactionStorage().findByXid(transactionQuery);

        if (transaction != null) {
            transaction.setStatus(transactionContext.getStatus());
            threadLocalTransaction.set(transaction);
        } else {
            throw new TransactionNotExistException();
        }
    }

    @Override
    public void commit(boolean aysnc) {
        Transaction transaction = getCurrentTransaction();

        transaction.setStatus(TransactionStatus.CONFIRMING);
        transactionConfiguration.getTransactionStorage().update(transaction);

        try {
            if(aysnc){
                Transaction transactionClone = transaction.clone();
                transactionConfiguration.getTransactionAysncExecutor().submit(()->{
                    transactionClone.commit();
                    int result = transactionConfiguration.getTransactionStorage().delete(transactionClone);
                    log.info("delete transaction {},result:{}",transactionClone,result);
                });
            }else{
                transaction.commit();
                int result = transactionConfiguration.getTransactionStorage().delete(transaction);
                log.info("delete transaction {},result:{}",transaction,result);
            }
            threadLocalTransaction.remove();
        } catch (Throwable commitException) {
            throw new ConfirmingException(commitException);
        }
    }

    @Override
    public void rollback(boolean aysnc) {
        Transaction transaction = getCurrentTransaction();

        transaction.setStatus(TransactionStatus.CANCELLING);
        transactionConfiguration.getTransactionStorage().update(transaction);


        try {
            if(aysnc){
                Transaction transactionClone = transaction.clone();
                transactionConfiguration.getTransactionAysncExecutor().submit(()->{
                    transactionClone.rollback();
                    int result = transactionConfiguration.getTransactionStorage().delete(transactionClone);
                    log.info("delete transaction {},result:{}",transactionClone,result);
                });
            }else{
                transaction.rollback();
                int result = transactionConfiguration.getTransactionStorage().delete(transaction);
                log.info("delete transaction {},result:{}",transaction,result);
            }
            threadLocalTransaction.remove();
        } catch (Throwable commitException) {
            throw new CancellingException(commitException);
        }
    }
}
