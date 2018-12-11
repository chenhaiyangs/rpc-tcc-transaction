package com.chenhaiyang.tcc.transaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 添加事务参与者登记handler
 * @author chenhaiyang
 */
public interface AddParticipanthandler {
    /**
     * 处理添加事务处理者的逻辑
     * @param proceedingJoinPoint 切点
     * @param method 方法对象
     */
    void process(ProceedingJoinPoint proceedingJoinPoint,Method method);
}
