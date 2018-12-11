package com.chenhaiyang.tcc.transaction.dashboard.component.vo;

import lombok.Data;

/**
 * 登陆过的账户自动提醒
 * @author chenhaiyang
 */
@Data
public class Tip {
    private String value;
    private String data;

    public Tip(String value, String data) {
        this.value = value;
        this.data = data;
    }
}
