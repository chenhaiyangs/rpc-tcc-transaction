package com.chenhaiyang.tcc.transaction.root.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * root方法的request函数
 * @author chenhaiyang
 */
@Data
@AllArgsConstructor
public class RootRequest implements Serializable{

    private String result;
}
