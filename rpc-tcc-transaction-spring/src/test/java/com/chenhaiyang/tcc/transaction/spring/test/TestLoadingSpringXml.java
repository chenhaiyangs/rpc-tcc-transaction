package com.chenhaiyang.tcc.transaction.spring.test;

import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.vo.InvocationContext;
import com.chenhaiyang.tcc.transaction.api.vo.Participant;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

public class TestLoadingSpringXml {

    /**
     * 不报异常则表示Spring没有错误
     */
    @Test
    public void test(){

        String domain="testredishaha";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"tcc-transaction.xml"});
        context.registerShutdownHook();
        context.start();
        TransactionStorage transactionStorage = (TransactionStorage) context.getBean("transactionStorage");

        TransactionXid xid = new TransactionXid(IdUtils.newId(),IdUtils.newId());
        Transaction transaction = new Transaction(xid, TransactionStatus.TRYING, TransactionType.ROOT);
        transaction.setDomain(domain);
        transactionStorage.create(transaction);
        System.out.println("存储transaction成功："+transaction);
        String[] classes = new String[1];
        classes[0]=TransactionXid.class.getName();
        Object[] pra = new Object[1];
        pra[0]=xid;
        InvocationContext cirCOntext = new InvocationContext(String.class.getName(),"toStringconfirm",classes,pra);
        InvocationContext cancelContext = new InvocationContext(String.class.getName(),"toStringcancel",classes,pra);
        Participant participant = new Participant(xid,cirCOntext,cancelContext);
        transaction.addParticipant(participant);
        transaction.setStatus(TransactionStatus.CONFIRMING);
        transactionStorage.update(transaction);
        System.out.println("更新成功："+transaction);

        transaction = transactionStorage.findByXid(transaction);

        System.out.println("查询结果："+transaction);

//        int result =  transactionStorage.delete(transaction);
//        System.out.println("删除结果："+result);

        List<Transaction> findUnProcess = transactionStorage.findAllUnProcessTransaction(domain,new Date());
        System.out.println("批量查询结果："+findUnProcess);


    }
}
