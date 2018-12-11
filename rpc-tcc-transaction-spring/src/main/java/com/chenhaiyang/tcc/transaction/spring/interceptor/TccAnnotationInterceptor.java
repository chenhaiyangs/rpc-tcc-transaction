package com.chenhaiyang.tcc.transaction.spring.interceptor;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.TransactionManager;
import com.chenhaiyang.tcc.transaction.api.enums.MethodType;
import com.chenhaiyang.tcc.transaction.api.exception.OptimisticLockException;
import com.chenhaiyang.tcc.transaction.api.exception.TransactionNotExistException;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.core.matcher.MethodMatcher;
import com.chenhaiyang.tcc.transaction.core.util.ReflectionUtils;
import com.chenhaiyang.tcc.transaction.spring.util.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 拦截tcc事务注解的interceptor
 * @author chenhaiyang
 */
@Slf4j
@Component
public class TccAnnotationInterceptor {
    /**
     * 事务配置器
     */
    private TransactionConfiguration transactionConfiguration;
    @Autowired
    public TccAnnotationInterceptor(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    /**
     * 处理切面拦截的注解
     * @param pjp pjg
     * @return 返回执行结果
     */
    public Object process(ProceedingJoinPoint pjp) throws Throwable {

        try {
            TransactionContext transactionContext = MethodMatcher.getTransactionContext(pjp.getArgs());

            TccTransaction tccTransaction = AopUtil.getAnnotation(pjp);
            boolean aysnc = tccTransaction.async();

            MethodType methodType = MethodMatcher.calculateMethodType(transactionContext, true);
            switch (methodType) {
                case ROOT:
                    return rootTransactionProcess(pjp, aysnc);
                case PROVIDER:
                    return providerTransactionProcess(pjp, transactionContext);
                default:
                    return pjp.proceed();
            }
        }catch (Throwable e){
            log.error("tccAnnotationInterceptor process error,pjp:{}",pjp,e);
            throw e;
        }
    }

    /**
     * 执行主事务逻辑
     * @param pjp 切点
     * @param aysnc 是否异步执行
     * @return 返回结果
     */
    private Object rootTransactionProcess(ProceedingJoinPoint pjp, boolean aysnc) throws Throwable {

        TransactionManager transactionManager = transactionConfiguration.getTransactionManager();
        transactionManager.begin();

        Object returnValue;
        try {
            //Try (开始执行被拦截的方法)
            returnValue = pjp.proceed();
        } catch (OptimisticLockException e) {
            throw e;
        } catch (Throwable tryingException) {
            transactionManager.rollback(aysnc);
            throw tryingException;
        }
        //Try检验正常后提交(事务管理器在控制提交)
        transactionManager.commit(aysnc);
        return returnValue;
    }

    /**
     * 执行分支事务提供者逻辑
     * @param pjp 切点
     * @param transactionContext 事务上下文
     * @return 返回结果
     */
    private Object providerTransactionProcess(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {

        switch (transactionContext.getStatus()) {
            case TRYING:
                // 基于全局事务ID扩展创建新的分支事务，并存于当前线程的事务局部变量中.
                transactionConfiguration.getTransactionManager().begin(transactionContext);
                return pjp.proceed();
            case CONFIRMING:
                try {
                    // 找出存在的事务并提交
                    transactionConfiguration.getTransactionManager().changeTransactionStatus(transactionContext);
                    transactionConfiguration.getTransactionManager().commit(false);
                } catch (TransactionNotExistException excepton) {
                    //事务可能已经被提交，忽略该异常
                }
                break;
            case CANCELLING:
                try {
                    //找出存在的事务并处理
                    transactionConfiguration.getTransactionManager().changeTransactionStatus(transactionContext);
                    transactionConfiguration.getTransactionManager().rollback(false);
                } catch (TransactionNotExistException exception) {
                    //事务可能已经被回滚，忽略该异常
                }
                break;
            default:
                break;
        }
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
        return ReflectionUtils.getNullValue(method.getReturnType());
    }
}
