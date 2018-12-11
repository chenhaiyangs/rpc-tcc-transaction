package com.chenhaiyang.tcc.transaction.core.storage.jdbc;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.OptimisticLockException;
import com.chenhaiyang.tcc.transaction.api.exception.TransactionIoException;
import com.chenhaiyang.tcc.transaction.api.vo.Transaction;
import com.chenhaiyang.tcc.transaction.core.storage.AbstractStorage;
import lombok.Setter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于jdbc实现的事务存储
 * @author chenhaiyang
 */
public class JdbcTransactionStorage extends AbstractStorage{

    public JdbcTransactionStorage(TransactionSerializer transactionSerializer) {
        super(transactionSerializer);
    }
    /**
     * 事务库的表后缀，不能为空
     */
    @Setter
    private String tbSuffix;
    /**
     * jdbc事务数据源
     */
    @Setter
    private DataSource dataSource;

    /**
     * 封装执行sql的操作
     * @param sql sql语句
     * @param mapper mapper
     * @param <R> 返回值
     * @return 返回结果
     */
    private <R> R execute(String sql,Function<PreparedStatement,? extends R> mapper){
        Connection connection=null;
        PreparedStatement stmt=null;
        try {
            connection =dataSource.getConnection();
            stmt=connection.prepareStatement(sql);
            return mapper.apply(stmt);
        } catch (SQLException e) {
            throw new TransactionIoException(e);
        } finally {
            release(stmt,connection,null);
        }
    }

    /**
     * 封装查询sql的原语
     * @param sql sql
     * @param prepaement 预编译对象渲染器
     * @return 返回结果
     */
    private List<Transaction> query(String sql,Consumer<PreparedStatement> prepaement){
        Connection connection=null;
        PreparedStatement stmt=null;
        ResultSet resultSet = null;
        try {
            connection =dataSource.getConnection();
            stmt=connection.prepareStatement(sql);
            prepaement.accept(stmt);
            resultSet =stmt.executeQuery();

            ResultSetMetaData md = resultSet.getMetaData();
            int columnCount = md.getColumnCount();
            List<Map<String,Object>> rows= new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>(10);
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }
                rows.add(rowData);
            }
            return rows.stream()
                    .map(this::convertToTransaction)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new TransactionIoException(e);
        } finally {
            release(stmt,connection,resultSet);
        }
    }

    /**
     * 将查询结果转换为transaction
     */
    private Transaction convertToTransaction(Map<String, Object> stringObjectMap) {
        byte[] content = (byte[]) stringObjectMap.get("content");
        return transactionSerializer.deserialize(content,Transaction.class);
    }

    /**
     * 释放连接
     * @param stmt stmt
     * @param connection conn
     * @param resultSet resultset
     */
    private void release(PreparedStatement stmt, Connection connection, ResultSet resultSet) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            if(resultSet!=null && !resultSet.isClosed()){
                resultSet.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 获取事务日志存储表名 前缀+suffix
     * @return 返回结果
     */
    private String getTableName(){
        return "tcc_transaction"+tbSuffix;

    }

    @Override
    public int init() {
        String sql ="CREATE TABLE IF NOT EXISTS `%s` (\n" +
                "  `transaction_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `domain` varchar(100) NOT NULL COMMENT '事务参与方标识，分布式事务，一个事务参与方domain',\n" +
                "  `global_tx_id` varbinary(32) NOT NULL DEFAULT '' COMMENT '全局事务id',\n" +
                "  `branch_qualifier` varbinary(32) NOT NULL DEFAULT '' COMMENT '分支事务id',\n" +
                "  `content` varbinary(8000) NOT NULL COMMENT '二进制事务内容，存储的是整个事务对象',\n" +
                "  `status` int(11) NOT NULL COMMENT '事务状态',\n" +
                "  `transaction_type` int(11) NOT NULL COMMENT '事务类型：是主事务，还是分支事务',\n" +
                "  `retried_count` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数，默认是0。用于事务恢复时重试',\n" +
                "  `create_time` datetime NOT NULL COMMENT '事务创建日期',\n" +
                "  `last_update_time` datetime NOT NULL COMMENT '事务最后更新时间',\n" +
                "  `next_process_time` datetime NOT NULL COMMENT '事务下次恢复时爬取日期（用于支持重试时的指数退避算法）',\n" +
                "  `version` int(11) NOT NULL DEFAULT '1' COMMENT '乐观锁版本号，避免多个进程同时更新事务',\n" +
                "  PRIMARY KEY (`transaction_id`),\n" +
                "  UNIQUE KEY `ux_tx_binery` (`global_tx_id`,`branch_qualifier`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;";

        sql = String.format(sql,getTableName());

        return execute(sql,(stat)->{
            try {
                return stat.executeUpdate();
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
    }

    @Override
    public int create(Transaction transaction) {
        //insert的sql
        String sql = String.format("insert into `%s`(domain,global_tx_id,branch_qualifier,content,status," +
                "transaction_type,retried_count,create_time,last_update_time," +
                "next_process_time,version) values(?,?,?,?,?,?,?,?,?,?,?);",getTableName());

        return execute(sql,(stat)->{
            try {

                stat.setObject(1,transaction.getDomain());
                stat.setObject(2,transaction.getXid().getGlobalTransactionId());
                stat.setObject(3,transaction.getXid().getBranchQualifier());
                stat.setObject(4,transactionSerializer.serialize(transaction));
                stat.setObject(5,transaction.getStatus().getId());
                stat.setObject(6,transaction.getTransactionType().getId());
                stat.setObject(7,transaction.getRetriesCount());
                stat.setObject(8,new Timestamp(transaction.getCreateTime().getTime()));
                stat.setObject(9,new Timestamp(transaction.getLastUpdateTime().getTime()));
                stat.setObject(10,new Timestamp(transaction.getNextProcessTime().getTime()));
                stat.setObject(11, transaction.getVersion());
                return stat.executeUpdate();
            } catch (SQLException e) {
               throw new TransactionIoException(e);
            }
        });
    }

    @Override
    public int update(Transaction transaction) {

        transaction.updateVersion();
        transaction.updateLastUpdateTime();
        //update的sql
        String sql = String.format("update `%s` set content=?,status=?,last_update_time=?,next_process_time=?,retried_count=?,version=version+1 " +
                "where  global_tx_id=? and branch_qualifier=? and version=?",getTableName());

        int result = execute(sql,(stat)->{
            try {
                stat.setObject(1,transactionSerializer.serialize(transaction));
                stat.setObject(2,transaction.getStatus().getId());
                stat.setObject(3,new Timestamp(transaction.getLastUpdateTime().getTime()));
                stat.setObject(4,new Timestamp(transaction.getNextProcessTime().getTime()));
                stat.setObject(5,transaction.getRetriesCount());
                stat.setObject(6,transaction.getXid().getGlobalTransactionId());
                stat.setObject(7,transaction.getXid().getBranchQualifier());
                stat.setObject(8,transaction.getVersion()-1);
                return stat.executeUpdate();
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
        if(result==FAIL){
           throw new OptimisticLockException();
        }
        return result;

    }

    @Override
    public int delete(Transaction transaction) {
        String sql=String.format("delete from `%s` where global_tx_id=? and branch_qualifier=? and domain=?",getTableName());
        return execute(sql,(stat)->{
            try {
                stat.setObject(1,transaction.getXid().getGlobalTransactionId());
                stat.setObject(2,transaction.getXid().getBranchQualifier());
                stat.setObject(3,transaction.getDomain());
                return stat.executeUpdate();
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
    }

    @Override
    public Transaction findByXid(Transaction transaction) {

        String sql=String.format("select content from `%s`"+
        "where global_tx_id=? and branch_qualifier=? and domain=? limit 1",getTableName());

        List<Transaction> transactions =  query(sql,(stmt)->{
            try {
                stmt.setObject(1,transaction.getXid().getGlobalTransactionId());
                stmt.setObject(2,transaction.getXid().getBranchQualifier());
                stmt.setObject(3,transaction.getDomain());
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
        if(transactions.size()>0){
            return transactions.get(0);
        }
        return null;
    }

    @Override
    public List<Transaction> findAllUnProcessTransaction(String domain,Date date) {

        String sql=String.format("select content from `%s` where " +
                "next_process_time < ? and transaction_type = 1 and domain=? limit 1000",getTableName());
        return query(sql,(stmt)->{
            try {
                stmt.setObject(1,new Timestamp(date.getTime()));
                stmt.setObject(2,domain);
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
    }

    @Override
    public List<Transaction> findUnProcessTransactionsWithDomain(String domainStr, int maxRetries) {
        String sql=String.format("select content from `%s` where " +
                "retried_count >= ? and transaction_type = 1 and domain like ? limit 1000",getTableName());
        return query(sql,(stmt)->{
            try {
                stmt.setObject(1,maxRetries);
                stmt.setObject(2,domainStr+"%");
            } catch (SQLException e) {
                throw new TransactionIoException(e);
            }
        });
    }
}
