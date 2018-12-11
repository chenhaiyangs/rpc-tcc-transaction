package com.chenhaiyang.tcc.transaction.spring.aspect;

import com.chenhaiyang.tcc.transaction.spring.interceptor.TccAnnotationInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

/**
 * 拦截Tcc注解的切面。主要是生成主事务和分支事务并执行提交和回滚动作
 * @author chenhaiyang
 */
@SuppressWarnings("all")
@Aspect
@Configuration
public class TccAnnotationAspect implements Ordered{
    /**
     * 处理该切面的注解函数
     */
    @Resource
    private TccAnnotationInterceptor tccAnnotationInterceptor;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Pointcut("@annotation(com.chenhaiyang.tcc.transaction.annotation.TccTransaction)")
    public void tccAnnotation() {

    }

    /**
     * 执行拦截动作
     * @param pjp 切点
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Around("tccAnnotation()")
    public Object interceptAnnotationMethod(ProceedingJoinPoint pjp) throws Throwable {
        return tccAnnotationInterceptor.process(pjp);
    }
}
