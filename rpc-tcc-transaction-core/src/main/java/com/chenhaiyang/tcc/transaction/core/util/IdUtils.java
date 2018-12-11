package com.chenhaiyang.tcc.transaction.core.util;

import java.util.UUID;

/**
 * 生成Id相关工具类
 * @author chenhaiyang
 */
public class IdUtils {
    /**
     * 初始化一个Id
     * @return 返回结果
     */
    public static byte[] newId() {
        return newUUId().getBytes();
    }

    /**
     * 生成一个uuid key
     * @return 返回结果
     */
    public static String newUUId() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
