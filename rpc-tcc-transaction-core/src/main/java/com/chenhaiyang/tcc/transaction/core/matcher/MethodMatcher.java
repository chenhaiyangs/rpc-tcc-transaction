package com.chenhaiyang.tcc.transaction.core.matcher;

import com.chenhaiyang.tcc.transaction.api.enums.MethodType;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;

/**
 * 和事务方法相关的决策类
 * @author chenhaiyang
 */
public class MethodMatcher {

    /**
     * 从参数获取事务上下文
     * @param args 参数
     * @return 结果
     */
    public static TransactionContext getTransactionContext(Object[] args) {
        TransactionContext transactionContext = null;
        for (Object arg : args) {
            if (arg != null && TransactionContext.class.isAssignableFrom(arg.getClass())) {
                transactionContext =(TransactionContext) arg;
            }
        }
        return transactionContext;
    }

    /**
     * 计算MethodType
     * @param transactionContext 事务上下文
     * @param hasAnnotation 是否存在注解
     * @return 返回执行结果
     */
    @SuppressWarnings("all")
    public static MethodType calculateMethodType(TransactionContext transactionContext, boolean hasAnnotation) {
        if (transactionContext == null && hasAnnotation) {
            return MethodType.ROOT;
        }
        if (transactionContext == null && !hasAnnotation) {
            return MethodType.CONSUMER;
        }
        if (transactionContext != null && hasAnnotation) {
            return MethodType.PROVIDER;
        }
        return MethodType.NONE;

    }
}
