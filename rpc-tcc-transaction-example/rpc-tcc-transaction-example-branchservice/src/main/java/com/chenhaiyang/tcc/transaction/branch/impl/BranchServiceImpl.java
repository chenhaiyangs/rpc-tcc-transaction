package com.chenhaiyang.tcc.transaction.branch.impl;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import com.chenhaiyang.tcc.transaction.context.TransactionContext;
import com.chenhaiyang.tcc.transaction.example.branch.api.BranchExampleApi;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Request;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Response;

/**
 * 分支事务dubbo服务提供者实现
 * 要求：try-confirm-cancel方法的入参必须完全相同
 * @author chenhaiyang
 */
public class BranchServiceImpl implements BranchExampleApi{

    /**
     * try方法上要添加TccTransaction注解，并标注confirmMethod的函数名和cancelMethod的函数名
     * async为可选注解，表示是否异步执行confirm方法或者cancel方法。只有主事务才生效
     * @param transactionContext 事务上下文
     * @param request 事务请求
     * @return 返回执行结果
     */
    @TccTransaction(confirmMethod = "branchconfirmMethod",cancelMethod = "branchcancelMethod")
    @Override
    public Response branchTryMethod(TransactionContext transactionContext, Request request) {


        System.out.println("我是try函数，我被执行了");
        System.out.println("transactionContext："+transactionContext);
        System.out.println("request："+request);

        return new Response("ok");
    }

    /**
     * 事务最终提交的方法。
     *
     * @param transactionContext 事务上下文
     * @param request 请求类
     * @return 返回结果
     */
    public Response branchconfirmMethod(TransactionContext transactionContext, Request request) {


        System.out.println("我是confirm提交函数，我被执行了");
        System.out.println("transactionContext："+transactionContext);
        System.out.println("request："+request);

        return new Response("ok");
    }

    /**
     * 事务最终提交的方法。
     *
     * @param transactionContext 事务上下文
     * @param request 请求类
     * @return 返回结果
     */
    public Response branchcancelMethod(TransactionContext transactionContext, Request request) {


        System.out.println("我是cancel回滚函数，我被执行了");
        System.out.println("transactionContext："+transactionContext);
        System.out.println("request："+request);

        return new Response("ok");
    }
}
