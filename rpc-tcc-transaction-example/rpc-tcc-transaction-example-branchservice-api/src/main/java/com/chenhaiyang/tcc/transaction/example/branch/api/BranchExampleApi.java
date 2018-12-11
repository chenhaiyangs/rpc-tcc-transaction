package com.chenhaiyang.tcc.transaction.example.branch.api;

import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Request;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Response;

/**
 * dubbo分支事务服务消费者。要求对提供者暴露try方法。并且要参数里要包含transactionContext 字段
 * @author chenhaiyang
 */
public interface BranchExampleApi {

    /**
     * 服务提供者的API里要有 transactionContext 参数
     * @param transactionContext 事务上下文
     * @param request 事务请求
     * @return 事务返回
     */
    Response branchTryMethod(TransactionContext transactionContext, Request request);
}
