package com.chenhaiyang.tcc.transaction.test;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.serializer.hessian.HessianSerializer;
import com.chenhaiyang.tcc.transaction.core.storage.zookeeper.ZookeeperTransactionStorage;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ZookeeperStorageTest {

    @Test
    public void test(){
        String dmoain="test";
        TransactionSerializer transactionSerializer = new HessianSerializer();
        TransactionStorage transactionStorage = new ZookeeperTransactionStorage(transactionSerializer,"127.0.0.1:2181");
        transactionStorage.init();
        TransactionXid xid = new TransactionXid(IdUtils.newId(),IdUtils.newId());
        Transaction transaction = new Transaction(xid, TransactionStatus.TRYING, TransactionType.ROOT);
        transaction.setDomain(dmoain);
        transactionStorage.create(transaction);

        System.out.println("生成的："+transaction);
        Transaction res = transactionStorage.findByXid(transaction);
        System.out.println("查询到的："+res);
        transactionStorage.update(transaction);
        Transaction res2 = transactionStorage.findByXid(transaction);
        System.out.println("查询到的："+res2.getVersion());
        int result = transactionStorage.delete(transaction);
        System.out.println("删除结果："+result);
        List<Transaction> result2 = transactionStorage.findAllUnProcessTransaction(dmoain,new Date());
        System.out.println(result2);
    }
}
