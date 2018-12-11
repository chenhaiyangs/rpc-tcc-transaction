package com.chenhaiyang.tcc.transaction.spring.handler;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.vo.InvocationContext;
import com.chenhaiyang.tcc.transaction.api.vo.Participant;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import com.chenhaiyang.tcc.transaction.core.util.ReflectionUtils;
import com.chenhaiyang.tcc.transaction.spring.AddParticipanthandler;
import com.chenhaiyang.tcc.transaction.spring.util.AopUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 登记跟事务的事务参与者
 * @author chenhaiyang
 */
@AllArgsConstructor
public class AddRootParticipant implements AddParticipanthandler{
    /**
     * 事务全局配置管理器
     */
    private TransactionConfiguration transactionConfiguration;


    @Override
    public void process(ProceedingJoinPoint pjp,Method method) {
        TccTransaction tccTransaction = AopUtil.getAnnotation(pjp);

        // 获取当前事务
        Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        // 获取事务Xid
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId(), IdUtils.newId());

        // 构建确认方法的提交上下文。confirmMethod和cancelMethod可能只有实现类里有，而try方法可能在接口里有
        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(),method.getName(), method.getParameterTypes());

        InvocationContext confirmInvocation = new InvocationContext(targetClass.getName(),
                tccTransaction.confirmMethod(),
                ReflectionUtils.getParameterStringArgs(method.getParameterTypes()), pjp.getArgs());

        // 构建取消方法的提交上下文
        InvocationContext cancelInvocation = new InvocationContext(targetClass.getName(),
                tccTransaction.cancelMethod(),
                ReflectionUtils.getParameterStringArgs(method.getParameterTypes()), pjp.getArgs());

        // 构建参与者对像
        Participant participant = new Participant(xid,confirmInvocation, cancelInvocation);

        transaction.addParticipant(participant);

        TransactionStorage transactionStorage = transactionConfiguration.getTransactionStorage();
        transactionStorage.update(transaction);
    }
}
