package com.chenhaiyang.tcc.transaction.core.task;

import com.chenhaiyang.tcc.transaction.api.RecoverConfig;
import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 事务恢复服务
 * @author chenhaiyang
 */
@Slf4j
public class TransactionRecoveryTask {
    /**
     * 当重试超过5次，则使用指数退避算法，下次重试的间隔将更久
     */
    private static final int LAZY_PROCESS=5;

    /**
     * TCC事务存储器
     */
    private TransactionStorage transactionStorage;
    /**
     * 事务恢复配置
     */
    private RecoverConfig recoverConfig;
    /**
     * 事务的业务域
     */
    private String domain;

    public TransactionRecoveryTask(TransactionConfiguration transactionConfiguration) {
        this.transactionStorage=transactionConfiguration.getTransactionStorage();
        this.recoverConfig=transactionConfiguration.getRecoverConfig();
        this.domain=transactionConfiguration.getDmoain();
    }

    @PostConstruct
    public void start(){
        long timeBefore=System.currentTimeMillis() - recoverConfig.getRecoverDuration()*1000;
        List<Transaction> transactions =
                transactionStorage.findAllUnProcessTransaction(domain,new Date(timeBefore));
        Optional.ofNullable(transactions)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(transaction ->{
                    try{
                        return transactionStorage.update(transaction)>0;
                    }catch (Exception e){
                        return false;
                    }
                }).forEach(this::process);
    }

    /**
     * 进行事务恢复
     * @param transaction 事务
     */
    private void process(Transaction transaction) {
        log.info("find unprocess transaction:{}",transaction);
        if (transaction.getRetriesCount() > recoverConfig.getMaxRetryCount()) {
            // 超过次数的，跳过
            log.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriesCount()));
            return;
        }
        try {
            transaction.setRetriesCount(transaction.getRetriesCount() + 1);

            long nextDateTime = System.currentTimeMillis();

            if(transaction.getRetriesCount()>= LAZY_PROCESS){
                nextDateTime = nextDateTime+(transaction.getRetriesCount()-LAZY_PROCESS)*recoverConfig.getNextRetriesAddTime()*1000;
            }
            transaction.setNextProcessTime(new Date(nextDateTime));
            if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                // 如果是CONFIRMING(2)状态，则将事务往前执行
                transaction.setStatus(TransactionStatus.CONFIRMING);
                int result = transactionStorage.update(transaction);
                log.info(String.format("update transaction %s,result:%s",transaction,result));
                transaction.commit();
                // 其他情况下，超时没处理的事务日志直接删除
                transactionStorage.delete(transaction);
            } else {
                // 其他情况，把事务状态改为CANCELLING(3)，然后执行回滚
                transaction.setStatus(TransactionStatus.CANCELLING);
                int result = transactionStorage.update(transaction);
                log.info(String.format("update transaction %s,result:%s",transaction,result));
                transaction.rollback();
                // 其他情况下，超时没处理的事务日志直接删除
                transactionStorage.delete(transaction);
            }
        }catch (Throwable e){
            log.error(String.format("recover failed, txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriesCount()), e);
        }
    }
}
