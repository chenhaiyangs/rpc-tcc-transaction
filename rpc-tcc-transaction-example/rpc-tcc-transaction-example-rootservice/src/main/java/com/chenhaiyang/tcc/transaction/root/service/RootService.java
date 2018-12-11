package com.chenhaiyang.tcc.transaction.root.service;

import com.chenhaiyang.tcc.transaction.annotation.TccTransaction;
import com.chenhaiyang.tcc.transaction.example.branch.api.BranchExampleApi;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Request;
import com.chenhaiyang.tcc.transaction.example.branch.vo.Response;
import com.chenhaiyang.tcc.transaction.root.vo.RootRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Dubbo 分布式事务root的root服务
 * @author chenhaiyang
 */
@Service
public class RootService {

    @Resource
    private BranchExampleApi branchExampleApi;

    @TccTransaction(confirmMethod = "rootconfirmMethod",cancelMethod = "rootcancelMethod",async = true)
    public String rootTryMethod(RootRequest msg){
        System.out.println("我是root服务的try方法，我的参数："+msg);

        Request request = new Request();
        request.setName("chenhaiyang");
        request.setRemark("我是tcc分布式事务Demo");
        request.setHello("我是hello");
        Response response = branchExampleApi.branchTryMethod(null,request);
        System.out.println("我调用分支事务的try方法的响应："+response);
        return "ok";
    }

    public String rootconfirmMethod(RootRequest msg){
        System.out.println("我是root服务的comfirm方法，我的参数："+msg);
        return "ok";
    }

    public String rootcancelMethod(RootRequest msg){
        System.out.println("我是root服务的cancel方法，我的参数："+msg);
        return "ok";
    }

}
