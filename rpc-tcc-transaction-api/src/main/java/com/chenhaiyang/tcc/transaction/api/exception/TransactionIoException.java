package com.chenhaiyang.tcc.transaction.api.exception;

/**
 * 事务IO异常
 * @author chenhaiyang
 */
public class TransactionIoException extends RuntimeException{
    public TransactionIoException(Throwable cause) {
        super(cause);
    }
}
