package com.chenhaiyang.tcc.transaction.api.exception;

/**
 * TCC事务抛出的异常
 * @author chenhaiyang
 */
public class TccException extends RuntimeException{
    public TccException(Throwable cause) {
        super(cause);
    }
    public TccException(String s) {
        super(s);
    }
}
