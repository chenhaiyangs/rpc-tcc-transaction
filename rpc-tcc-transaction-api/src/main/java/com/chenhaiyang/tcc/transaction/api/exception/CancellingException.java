package com.chenhaiyang.tcc.transaction.api.exception;

/**
 * 取消异常
 * @author chenhaiyang
 */
public class CancellingException extends RuntimeException{
    public CancellingException(Throwable cause) {
        super(cause);
    }
}
