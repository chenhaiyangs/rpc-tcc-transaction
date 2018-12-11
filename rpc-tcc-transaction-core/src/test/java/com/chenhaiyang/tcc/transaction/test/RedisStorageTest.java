package com.chenhaiyang.tcc.transaction.test;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.serializer.hessian.HessianSerializer;
import com.chenhaiyang.tcc.transaction.core.storage.redis.RedisTransactionStorage;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;

public class RedisStorageTest {

    @Test
    public void test(){

        JedisPool jedisPool = new JedisPool("127.0.0.1",6379);
        String domain="test2";

        TransactionSerializer transactionSerializer = new HessianSerializer();
        RedisTransactionStorage transactionStorage = new RedisTransactionStorage(transactionSerializer);
        transactionStorage.setJedisPool(jedisPool);
        transactionStorage.setLockTimeout(20000);

        transactionStorage.init();
        TransactionXid xid = new TransactionXid(IdUtils.newId(),IdUtils.newId());
        Transaction transaction = new Transaction(xid, TransactionStatus.TRYING, TransactionType.ROOT);
        transaction.setDomain(domain);
        transactionStorage.create(transaction);

        System.out.println("生成的："+transaction);
        Transaction res = transactionStorage.findByXid(transaction);
        System.out.println("查询到的："+res);
        transactionStorage.update(transaction);
        Transaction res2 = transactionStorage.findByXid(transaction);
        System.out.println("查询到的："+res2.getVersion());

        //int result2 = transactionStorage.delete(transaction);
        //System.out.println(result2);
        List<Transaction> result = transactionStorage.findAllUnProcessTransaction(domain,new Date());
        System.out.println(result);
    }


}
