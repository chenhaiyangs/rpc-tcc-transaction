package com.chenhaiyang.tcc.transaction.dashboard.component.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Desc : 示例{"resData":"{"userID":""}","resCode":"0000","resMessage":"成功"}
 * @author chenhaiyang
 */
@Data
@NoArgsConstructor
public class ResultEntity<T> {
    private T resData;
    private String resCode;
    private String resMessage;

    public ResultEntity(ResultCode resultCode){
        this.setResCode(resultCode.getCode());
        this.setResMessage(resultCode.getMsg());
    }

    public  void setCodeMsg(ResultCode resultCode){
        this.setResCode(resultCode.getCode());
        this.setResMessage(resultCode.getMsg());
    }
}
