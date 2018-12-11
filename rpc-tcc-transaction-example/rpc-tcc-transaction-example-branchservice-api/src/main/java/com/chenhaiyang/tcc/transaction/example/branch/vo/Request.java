package com.chenhaiyang.tcc.transaction.example.branch.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求对象
 * @author chenhaiyang
 */
@Data
public class Request implements Serializable{

    private String name;

    private String remark;

    private String hello;
}
