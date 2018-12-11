package com.chenhaiyang.tcc.transaction.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhaiyang.tcc.transaction.api.TransactionStorage;
import com.chenhaiyang.tcc.transaction.api.enums.TransactionType;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionStatus;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.dashboard.component.vo.ResultEntity;
import com.chenhaiyang.tcc.transaction.dashboard.page.DataGrid;
import com.chenhaiyang.tcc.transaction.dashboard.component.vo.ResultCode;
import com.chenhaiyang.tcc.transaction.dashboard.page.OperateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 事务查询列表
 * @author chenhaiyang
 */
@RestController
@RequestMapping(value = "/transaction")
@Slf4j
public class TransactionController {
    @Resource
    private TransactionStorage transactionStorage;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ModelAndView findTransactions(){
        return new ModelAndView("transaction_list");
    }
    /**
     * 获取模型列表
     * @return 模型
     */
    @ResponseBody
    @RequestMapping(value="/list",method = RequestMethod.POST)
    public String list(HttpServletRequest request) {

        String domain = ServletRequestUtils.getStringParameter(request,"domain","");
        int maxRetries = ServletRequestUtils.getIntParameter(request,"maxRetries",0);
        DataGrid modelPage = new DataGrid(0,null);

        try {
            List<Transaction> transactionList = transactionStorage.findUnProcessTransactionsWithDomain(domain,maxRetries);
            List<OperateVo> transactionLists = Optional.ofNullable(transactionList)
                    .map(List::stream)
                    .orElseGet(Stream::empty)
                    .map(OperateVo::cast)
                    .collect(Collectors.toList());

            modelPage.setTotal(transactionLists.size());
            modelPage.setRows(transactionLists);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return JSON.toJSONString(modelPage);
    }

    /**
     * 删除事务信息
     * @param transactions 事务列表
     * @return 返回
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public String delete(@RequestBody List<OperateVo> transactions){

        ResultEntity<String> resultEntity = new ResultEntity<>(ResultCode.COMMON_SUCCESS);
        try{
            transactions.stream()
                    .filter(operateVo -> operateVo.getDomain()!=null && operateVo.getDomain().length()>0
                            && operateVo.getGlobalTransactionId()!=null && operateVo.getGlobalTransactionId().length()>0
                            && operateVo.getBranchQualifier()!=null && operateVo.getBranchQualifier().length()>0
                    )
                    .map(OperateVo::toTransaction)
                    .forEach(transaction -> transactionStorage.delete(transaction));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            resultEntity.setCodeMsg(ResultCode.COMMON_FAIL);
        }

        return JSONObject.toJSONString(resultEntity);
    }

    /**
     * 跳转到事务详情信息查看页面
     * @return 返回结果
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public ModelAndView transactionDetail(HttpServletRequest request){
        String domain = ServletRequestUtils.getStringParameter(request,"domain","");
        String globalTransactionId = ServletRequestUtils.getStringParameter(request,"globalTransactionId","");
        String branchQualifier = ServletRequestUtils.getStringParameter(request,"branchQualifier","");

        TransactionXid transactionXid = new TransactionXid(globalTransactionId.getBytes(),branchQualifier.getBytes());
        Transaction transaction = new Transaction(transactionXid, TransactionStatus.TRYING, TransactionType.ROOT);
        transaction.setDomain(domain);

        Transaction result =  transactionStorage.findByXid(transaction);
        ModelAndView mv = new ModelAndView("transaction_detail");
        String json = JSONObject.toJSONString(result,true);
        mv.addObject("transaction",json);
        mv.addObject("domain",domain);
        mv.addObject("globalTransactionId",globalTransactionId);
        mv.addObject("branchQualifier",branchQualifier);
        return mv;
    }

    /**
     * 清空重试次数
     * @return 返回结果
     */
    @RequestMapping(value = "/reset",method = RequestMethod.POST)
    public String reset(@RequestBody OperateVo vo){
        Integer result;
        if(StringUtils.isBlank(vo.getDomain())
                ||StringUtils.isBlank(vo.getBranchQualifier())
                ||StringUtils.isBlank(vo.getGlobalTransactionId())){
            return JSONObject.toJSONString(new ResultEntity<>(ResultCode.COMMON_FAIL));
        }
        try {
            Transaction transaction = OperateVo.toTransaction(vo);
            transaction = transactionStorage.findByXid(transaction);
            if(transaction==null){
                throw new RuntimeException("transaction==null");
            }
            transaction.setRetriesCount(0);
            result = transactionStorage.update(transaction);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return JSONObject.toJSONString(new ResultEntity<>(ResultCode.COMMON_FAIL));
        }
        ResultEntity<String> resultEntity = new ResultEntity<>(ResultCode.COMMON_SUCCESS);
        resultEntity.setResData(result+"");
        return JSONObject.toJSONString(resultEntity);
    }



}
