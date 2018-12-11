package com.chenhaiyang.tcc.transaction.spring.handler;

import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.vo.InvocationContext;
import com.chenhaiyang.tcc.transaction.api.vo.Participant;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import com.chenhaiyang.tcc.transaction.core.util.ReflectionUtils;
import com.chenhaiyang.tcc.transaction.spring.AddParticipanthandler;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 添加跟任务参与者的消费者任务
 * @author chenhaiyang
 */
@AllArgsConstructor
public class AddConsumerParticipant implements AddParticipanthandler{
    /**
     * 事务全局配置管理器
     */
    private TransactionConfiguration transactionConfiguration;

    @Override
    public void process(ProceedingJoinPoint pjp,Method method) {

        // 获取当前事务
        Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        // 获取事务Xid
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId(), IdUtils.newId());

        int position = ReflectionUtils.getParamPosition(((MethodSignature) pjp.getSignature()).getParameterTypes(), TransactionContext.class);


        // 构建事务上下文
        // 给服务接口的TransactionContext参数设值。transactionContext会传递到消费者端
        pjp.getArgs()[position] = new TransactionContext(xid, transaction.getStatus());

        Object[] tryArgs = pjp.getArgs();
        Object[] confirmArgs = new Object[tryArgs.length];
        Object[] cancelArgs = new Object[tryArgs.length];

        System.arraycopy(tryArgs, 0, confirmArgs, 0, tryArgs.length);
        confirmArgs[position] = new TransactionContext(xid, TransactionStatus.CONFIRMING);

        System.arraycopy(tryArgs, 0, cancelArgs, 0, tryArgs.length);
        cancelArgs[position] = new TransactionContext(xid, TransactionStatus.CANCELLING);


        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());

        // 构建确认方法的提交上下文
        InvocationContext confirmInvocation = new InvocationContext(targetClass.getName(),
                method.getName(),
                ReflectionUtils.getParameterStringArgs(method.getParameterTypes()), confirmArgs);

        // 构建取消方法的提交上下文
        InvocationContext cancelInvocation = new InvocationContext(targetClass.getName(),
                method.getName(),
                ReflectionUtils.getParameterStringArgs(method.getParameterTypes()),cancelArgs);

        // 构建参与者对像
        Participant participant = new Participant(xid,confirmInvocation, cancelInvocation);

        transaction.addParticipant(participant);

        TransactionStorage transactionStorage = transactionConfiguration.getTransactionStorage();
        transactionStorage.update(transaction);
    }
}
