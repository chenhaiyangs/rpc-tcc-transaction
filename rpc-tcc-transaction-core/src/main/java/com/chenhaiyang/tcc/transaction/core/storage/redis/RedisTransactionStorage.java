package com.chenhaiyang.tcc.transaction.core.storage.redis;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.OptimisticLockException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.core.storage.AbstractStorage;
import com.chenhaiyang.tcc.transaction.core.storage.redis.support.RedisTransactionHelper;
import lombok.Setter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用redis作为事务存储的介质
 * @author chenhaiyang
 */
public class RedisTransactionStorage extends AbstractStorage{
    /**
     * 事务业务前缀
     */
    private static final String KEY_PREFIX="tcc_transaction_";
    /**
     * 执行update的分布式锁时常，默认2秒（单位：毫秒）
     */
    @Setter
    private int lockTimeout=2000;
    /**
     * redis事务存储帮助工具类
     */
    private RedisTransactionHelper redisTransactionHelper;
    public void setJedisPool(JedisPool jedisPool){
        this.redisTransactionHelper=new RedisTransactionHelper(jedisPool);
    }

    public RedisTransactionStorage(TransactionSerializer transactionSerializer) {
        super(transactionSerializer);
    }

    @Override
    public int init() {
        return SUCCESS;
    }

    @Override
    public int create(Transaction transaction) {
        byte[] key = redisTransactionHelper.getTransactionKey(KEY_PREFIX,transaction);
        return redisTransactionHelper.getResult(jedis -> {

            long result = jedis.setnx(key,transactionSerializer.serialize(transaction));
            return Integer.parseInt(Long.valueOf(result).toString());
        });
    }

    @Override
    public int update(Transaction transaction) {
        byte[] key = redisTransactionHelper.getTransactionKey(KEY_PREFIX,transaction);
        return redisTransactionHelper.requireLock(lockTimeout,transaction,jedis -> {

            transaction.updateVersion();
            transaction.updateLastUpdateTime();

            String result = jedis.set(key,transactionSerializer.serialize(transaction));
            if(RedisTransactionHelper.OPERATE_SUCCESS.equals(result)){
                return SUCCESS;
            }
            throw new OptimisticLockException();
        });
    }

    @Override
    public int delete(Transaction transaction) {
        byte[] key = redisTransactionHelper.getTransactionKey(KEY_PREFIX,transaction);
        return redisTransactionHelper.getResult(jedis -> {
            long result = jedis.del(key);
            return Integer.parseInt(Long.valueOf(result).toString());
        });
    }

    @Override
    public Transaction findByXid(Transaction transaction) {
        byte[] key = redisTransactionHelper.getTransactionKey(KEY_PREFIX,transaction);
        return redisTransactionHelper.getResult(jedis -> {
            byte[] results = jedis.get(key);
            if(results!=null){
                return transactionSerializer.deserialize(results,Transaction.class);
            }else {
                return null;
            }
        });
    }

    @Override
    public List<Transaction> findAllUnProcessTransaction(String domain,Date date) {
        List<String> results = scanKeys(domain);
        return Optional.ofNullable(results)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(this::toTransaction)
                .filter(Objects::nonNull)
                .filter(transaction -> transaction.getNextProcessTime().getTime()<date.getTime())
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findUnProcessTransactionsWithDomain(String domainStr, int maxRetries) {
       List<String> results = scanKeys(domainStr);

        return Optional.ofNullable(results)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(this::toTransaction)
                .filter(Objects::nonNull)
                .filter(transaction -> transaction.getRetriesCount()>=maxRetries)
                .collect(Collectors.toList());
    }

    /**
     * 一次扫描1000个key做处理
     * @param domainStr domain
     * @return 返回结果
     */
    private List<String> scanKeys(String domainStr){
       return redisTransactionHelper.getResult(jedis ->{
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanResult<String> scanResults = jedis.scan(cursor,new ScanParams().count(1000).match(KEY_PREFIX+domainStr+"*"));
            if(!scanResults.getResult().isEmpty()){
                return scanResults.getResult();
            }
            return new ArrayList<>();
        });
    }

    private Transaction toTransaction(String key) {
        return redisTransactionHelper.getResult(jedis -> {
            byte[] results = jedis.get(key.getBytes());
            if(results!=null){
                return transactionSerializer.deserialize(results,Transaction.class);
            }else{
                return null;
            }
        });
    }
}
