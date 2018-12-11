package com.chenhaiyang.tcc.transaction.core.storage.zookeeper;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.OptimisticLockException;
import com.chenhaiyang.tcc.transaction.api.exception.TransactionIoException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.context.TransactionXid;
import com.chenhaiyang.tcc.transaction.core.storage.AbstractStorage;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * zookeeper作为存储介质
 * @author chenhaiyang
 */
public class ZookeeperTransactionStorage extends AbstractStorage{
    /**
     * tcc分布式事务在zookeeper下的存储节点
     */
    private static final String TCC_ROOT="/tcc";
    /**
     * zk客户端
     */
    @Setter
    private CuratorFramework client = null;

    public ZookeeperTransactionStorage(TransactionSerializer transactionSerializer,String zookeeperUrl) {
        super(transactionSerializer);

        Objects.requireNonNull(zookeeperUrl,"zookeeper url must not be null");
        client = CuratorFrameworkFactory.newClient(zookeeperUrl,new ExponentialBackoffRetry(1000, 3));
    }

    @Override
    public int init() {
        client.start();
        return SUCCESS;
    }

    @Override
    public int create(Transaction transaction) {
        try {

            String path = getPath(transaction);
            client.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path,transactionSerializer.serialize(transaction));
            return SUCCESS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int update(Transaction transaction) {
        try {
            transaction.updateVersion();
            transaction.updateLastUpdateTime();

            String path = getPath(transaction);
            //通过setData的version实现乐观锁控制。事务对象的version从1开始，zkversion从0开始。所以要 -2
            client.setData()
                    .withVersion((int)transaction.getVersion()-2)
                    .forPath(path,transactionSerializer.serialize(transaction));

            return SUCCESS;
        }catch (KeeperException.BadVersionException version){
            throw  new OptimisticLockException();
        }catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int delete(Transaction transaction) {
        try {
            String path = getPath(transaction);
            client.delete().forPath(path);
            return SUCCESS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public Transaction findByXid(Transaction transaction) {
        try {
            String path = getPath(transaction);
            byte[] result = client.getData().forPath(path);
            if(result!=null){
                return transactionSerializer.deserialize(result,Transaction.class);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public List<Transaction> findAllUnProcessTransaction(String domain,Date date) {

        try {
            String path = getDomainPath(domain);
            List<String> result = client.getChildren().forPath(path);
            return  Optional.ofNullable(result)
                    .map(List::stream)
                    .orElseGet(Stream::empty)
                    .map(key->convertToTransaction(path,key))
                    .filter(transaction -> transaction.getNextProcessTime().getTime()<date.getTime())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public List<Transaction> findUnProcessTransactionsWithDomain(String domainStr, int maxRetries) {
        try {
            String path = getDomainPath(domainStr);
            List<String> result = client.getChildren().forPath(path);
            return  Optional.ofNullable(result)
                    .map(List::stream)
                    .orElseGet(Stream::empty)
                    .map(key->convertToTransaction(path,key))
                    .filter(transaction -> transaction.getRetriesCount()>=maxRetries)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 将zookeeper上的结果转换为事务对象
     * @param key key
     * @return 结果
     */
    private Transaction convertToTransaction(String parent,String key) {
        try {
            byte[] result = client.getData().forPath(getChildPath(parent,key));
            return transactionSerializer.deserialize(result,Transaction.class);
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }
    private String getPath(Transaction transaction) {
        TransactionXid xid = transaction.getXid();
        return String.format("%s/%s/%s",TCC_ROOT,transaction.getDomain(), new String(xid.getGlobalTransactionId())+""+new String(xid.getBranchQualifier()));
    }
    private String getDomainPath(String domainStr) {
        return String.format("%s/%s",TCC_ROOT,domainStr);
    }
    private String getChildPath(String parent,String child) {
        return String.format("%s/%s",parent,child);
    }
}

