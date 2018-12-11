package com.chenhaiyang.tcc.transaction.spring.interceptor;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.enums.MethodType;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.core.matcher.MethodMatcher;
import com.chenhaiyang.tcc.transaction.spring.AddParticipanthandler;
import com.chenhaiyang.tcc.transaction.spring.handler.AddConsumerParticipant;
import com.chenhaiyang.tcc.transaction.spring.handler.AddProviderParticipant;
import com.chenhaiyang.tcc.transaction.spring.handler.AddRootParticipant;
import com.chenhaiyang.tcc.transaction.spring.util.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * tcc事务添加事务参与者拦截器
 * @author chenhaiyang
 */
@Slf4j
@Component
public class TccAddParticipantInterceptor {
    /**
     * 处理事务参与者登记的各种handler
     */
    private Map<MethodType,AddParticipanthandler> processes = new HashMap<>();
    /**
     * 事务配置器.
     */
    private TransactionConfiguration transactionConfiguration;

    @Autowired
    public TccAddParticipantInterceptor(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;

        processes.put(MethodType.ROOT,new AddRootParticipant(transactionConfiguration));
        processes.put(MethodType.CONSUMER,new AddConsumerParticipant(transactionConfiguration));
        processes.put(MethodType.PROVIDER,new AddProviderParticipant(transactionConfiguration));
    }


    /**
     * 执行具体的业务逻辑
     * @param pjp 切点
     * @return 返回执行结果
     */
    public Object process(ProceedingJoinPoint pjp) throws Throwable {

        try {
            Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
            // Trying(判断是否Try阶段的事务)
            if (transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {
                // 从参数获取事务上下文
                TransactionContext transactionContext = MethodMatcher.getTransactionContext(pjp.getArgs());
                // 获取事务补偿注解
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                Method method = signature.getMethod();
                TccTransaction tccTransaction = AopUtil.getAnnotation(pjp);
                // 计算方法类型
                MethodType methodType = MethodMatcher.calculateMethodType(transactionContext, tccTransaction != null);

                AddParticipanthandler participanthandler = processes.get(methodType);
                if (participanthandler != null) {
                    participanthandler.process(pjp, method);
                }
            }
        }catch (Throwable e){
            log.error("tccAddParticipantInterceptor process error,pjp:{}",pjp,e);
            throw e;
        }
        return pjp.proceed(pjp.getArgs());
    }
}
