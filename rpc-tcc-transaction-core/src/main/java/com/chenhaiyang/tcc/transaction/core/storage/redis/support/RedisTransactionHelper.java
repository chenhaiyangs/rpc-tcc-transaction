package com.chenhaiyang.tcc.transaction.core.storage.redis.support;

import com.chenhaiyang.tcc.transaction.api.exception.OptimisticLockException;
import com.chenhaiyang.tcc.transaction.api.exception.TransactionIoException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.function.Function;

/**
 * jedis_transaction帮助工具类
 * @author chenhaiyang
 */
@Slf4j
public class RedisTransactionHelper {
    /**
     * 分布式锁前缀
     */
    private static final String KEY_PREFIX_LOCK="tcc_transaction_lock_";

    /**
     * 操作成功
     */
    public static final String OPERATE_SUCCESS="OK";
    /**
     * 只有当key不存在的时候，才set
     */
    private static final String SET_IF_NOT_EXISTS="NX";
    /**
     * 给key设置超时时间
     */
    private static final String SET_WITH_EXPIRE_TIME="PX";
    /**
     * 删除分布式锁的lua脚本
     * redis需要开启使用lua
     */
    private static final String DELET_LOCK_LUA="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * jedisPool
     */
    private JedisPool jedisPool;
    public RedisTransactionHelper(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 获取事务key前缀
     * @param prefixStr 业务key前缀
     * @param transaction 事务
     * @return 返回结果
     */
    public byte[] getTransactionKey(String prefixStr, Transaction transaction) {
        byte[] prefix = (prefixStr+transaction.getDomain()).getBytes();
        return getKey(prefix,transaction.getXid());
    }
    /**
     * 获取事务分布式锁前缀
     * @param xid xid
     * @param domain domain
     * @return 返回结果
     */
    private byte[] getLockKey(TransactionXid xid, String domain) {
        byte[] prefix = (KEY_PREFIX_LOCK+domain).getBytes();
        return getKey(prefix,xid);
    }

    /**
     * 拼接前缀
     * @param prefix prefix
     * @param xid xid
     * @return byte[]
     */
    private byte[] getKey(byte[] prefix,TransactionXid xid){
        String transactionId= new String(xid.getGlobalTransactionId());
        String branchId= new String(xid.getBranchQualifier());

        return String.format("%s%s%s",new String(prefix),transactionId,branchId).getBytes();
    }


    /**
     * 使用此方法执行各种有返回值的操作
     * 比如
     *     jedis.set(k,value);成功返回 "OK",失败返回 null
     *     jedis.set(key,value,"NX","PX",livetime);
     *     jedis.exist
     *     jedis.incr
     * @param mapper lambda
     * @param <R> 返回值类型
     */
    public <R> R getResult(Function<? super Jedis, ? extends R> mapper){
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            return mapper.apply(jedis);
        }catch (Throwable e){
           throw new TransactionIoException(e);
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    /**
     * 获取分布式锁后才能支持的操作
     * @param transaction 事务信息
     * @param mapper jedis回调
     * @return 影响的行数
     */
    public int requireLock(int maxLockTime,Transaction transaction,Function<? super Jedis,Integer> mapper) {
        Jedis jedis=null;
        byte[] lockKey=new byte[0];
        String rqeuestId=  IdUtils.newUUId();
        try {
            jedis = jedisPool.getResource();
            lockKey=getLockKey(transaction.getXid(),transaction.getDomain());
            String result= jedis.set(new String(lockKey),rqeuestId,SET_IF_NOT_EXISTS,SET_WITH_EXPIRE_TIME,maxLockTime);
            if(OPERATE_SUCCESS.equals(result)){
                return mapper.apply(jedis);
            }else {
               throw new OptimisticLockException();
            }
        }catch (Throwable e){
            throw new TransactionIoException(e);
        }finally {
            if(jedis!=null){
                try{
                    jedis.eval(DELET_LOCK_LUA, Collections.singletonList(new String(lockKey)), Collections.singletonList(rqeuestId));
                }finally {
                    jedis.close();
                }
            }
        }
    }
}
