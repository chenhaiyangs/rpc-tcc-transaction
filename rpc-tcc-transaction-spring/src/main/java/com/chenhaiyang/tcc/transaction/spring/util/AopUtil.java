package com.chenhaiyang.tcc.transaction.spring.util;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Aop工具类
 * @author chenhaiyang
 */
public class AopUtil {

    /**
     * 获取注解
     * @return 返回结果
     */
    public static TccTransaction getAnnotation(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        TccTransaction tccTransaction = method.getAnnotation(TccTransaction.class);

        if (tccTransaction == null) {
            Method targetMethod;
            try {
                // 获取目标方法
                targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
                if (targetMethod != null) {
                    tccTransaction = targetMethod.getAnnotation(TccTransaction.class);
                }
            } catch (NoSuchMethodException e) {
                tccTransaction = null;
            }
        }
        return tccTransaction;
    }
}
