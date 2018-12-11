package com.chenhaiyang.tcc.transaction.spring.aspect;

import com.chenhaiyang.tcc.transaction.spring.interceptor.TccAddParticipantInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

/**
 * tcc事务，添加事务参与者切面
 * @author chenhaiyang
 */
@SuppressWarnings("all")
@Aspect
@Configuration
public class TccAddParticipantAspect implements Ordered {
    /**
     * 处理该切面的注解，添加事务参与者
     */
    @Resource
    private TccAddParticipantInterceptor addParticipantInterceptor;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    /**
     * 定义切入点（包含切入点表达式和切点签名）.
     */
    @Pointcut("execution(public * *(..,com.chenhaiyang.tcc.transaction.context.TransactionContext,..))||@annotation(com.chenhaiyang.tcc.transaction.annotation.TccTransaction)")
    public void tccAddParticipant() {

    }

    /**
     * 添加事务参与者切面
     * @param pjp 切点
     * @return 返回执行结果
     * @throws Throwable 异常
     */
    @Around("tccAddParticipant()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
       return addParticipantInterceptor.process(pjp);
    }
}
