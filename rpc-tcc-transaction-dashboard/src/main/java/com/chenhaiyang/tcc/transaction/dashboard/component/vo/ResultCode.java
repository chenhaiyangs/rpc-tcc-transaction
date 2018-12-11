package com.chenhaiyang.tcc.transaction.dashboard.component.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务操作返回码
 * @author chenhaiyang
 */
@AllArgsConstructor
public enum  ResultCode {

    /**
     * 包含所有公共返回状态码
     */
    COMMON_SUCCESS("200","操作成功"),
    COMMON_FAIL("500","操作失败");

    @Getter
    private String code;
    @Getter
    private String msg;

}
