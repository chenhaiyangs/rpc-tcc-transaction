package com.chenhaiyang.tcc.transaction.api;

import com.chenhaiyang.tcc.transaction.api.vo.Transaction;

import java.util.Date;
import java.util.List;

/**
 * 事务持久化接口
 * @author chenhaiyang
 */
public interface TransactionStorage {

    /**
     * 事务持久化存储介质的初始化操作，例如，jdbc存储的建表
     * @return 返回结果
     */
    int init();
    /**
     * 创建事务日志记录.
     * @param transaction transaction
     * @return rowEffected
     */
    int create(Transaction transaction);

    /**
     * 更新事务日志记录.
     * @param transaction transaction
     * @return rowEffected
     */
    int update(Transaction transaction);

    /**
     * 删除事务日志记录.
     * @param transaction transaction
     * @return rowEffected
     */
    int delete(Transaction transaction);

    /**
     * 根据transaction信息 查找事务日志记录.
     * @param transaction  事务对象
     * @return transaction
     */
    Transaction findByXid(Transaction transaction);

    /**
     * 找出所有未处理事务日志（从某一时间点开始）,最多1000条
     * @param date 时间
     * @param domain 业务域
     * @return 返回所有需要恢复的事务
     */
    List<Transaction> findAllUnProcessTransaction(String domain,Date date);

    /**
     * 查询指定domain下重试次数超过n次的事务,最多1000条
     * @param domain domain域
     * @param maxRetries 最大重试次数
     * @return 返回列表
     */
    List<Transaction> findUnProcessTransactionsWithDomain(String domain,int maxRetries);
}
