### 一个具体的tcc-transaction.xml配置Demo:
具体详见下面xml文件里的各个注释：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd   http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task.xsd">

    <!-- 开启Spring对@AspectJ风格切面的支持(因为下面用到自定义的TCC补偿切面类) -->
    <!-- @Aspect注解不能被Spring自动识别并注册为Bean,因此要通过xml的bean配置,或通过@Compenent注解标识其为Spring管理Bean -->
    <aop:aspectj-autoproxy proxy-target-class="true"/>
    <!-- 启用定时任务注解 -->
    <task:annotation-driven/>
    <!-- 扫描tcc-transaction框架包 -->
    <context:component-scan base-package="com.chenhaiyang.tcc.transaction.spring"/>

    <!-- 事务序列化工具 -->
    <bean id="hessianSeralizer" class="com.chenhaiyang.tcc.transaction.core.serializer.hessian.HessianSerializer"/>

    <!-- TCC 业务活动日志（事务日志）的jdbc数据源 -->
    <bean id="tccDataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="clone">
        <!-- 基本属性driverClassName、 url、user、password -->
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/tcc-transaction?useUnicode=true" />
        <property name="username" value="root" />
        <property name="password" value="root" />

        <!-- 配置初始化大小、最小、最大 -->
        <!-- 通常来说，只需要修改initialSize、minIdle、maxActive -->
        <!-- 初始化时建立物理连接的个数，缺省值为0 -->
        <property name="initialSize" value="5" />
        <!-- 最小连接池数量 -->
        <property name="minIdle" value="10" />
        <!-- 最大连接池数量，缺省值为8 -->
        <property name="maxActive" value="20" />

        <!-- 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 -->
        <property name="maxWait" value="1000" />
    </bean>

    <!-- 事务持久化介质,自选,这里以jdbc为例 -->
    <bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.jdbc.JdbcTransactionStorage">
        <!-- 序列化方式 -->
        <constructor-arg ref="hessianSeralizer"/>
        <!-- jdbc数据源 -->
        <property name="dataSource" ref="tccDataSource"/>
        <!-- 数据库表名后缀 -->
        <property name="tbSuffix" value="_root"/>
    </bean>

    <!-- 事务持久化介质，自选，这里以redis为例 -->
    <!--<bean id="jedisPool" class="redis.clients.jedis.JedisPool">-->
        <!--<constructor-arg value="127.0.0.1"/>-->
        <!--<constructor-arg value="6379"/>-->
    <!--</bean>-->
    <!--<bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.redis.RedisTransactionStorage">-->
        <!--&lt;!&ndash; 序列化方式 &ndash;&gt;-->
        <!--<constructor-arg ref="hessianSeralizer"/>-->
        <!--&lt;!&ndash; 分布式锁超时时间（upadate原子性使用分布式锁实现） &ndash;&gt;-->
        <!--<property name="lockTimeout" value="2000"/>-->
        <!--&lt;!&ndash; jedis pool &ndash;&gt;-->
        <!--<property name="jedisPool" ref="jedisPool"/>-->
    <!--</bean>-->
    <!-- 事务持久化介质，自选，这里以zookeeper为例 -->
    <!--<bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.zookeeper.ZookeeperTransactionStorage">-->
    <!--&lt;!&ndash; 序列化方式 &ndash;&gt;-->
    <!--<constructor-arg ref="hessianSeralizer"/>-->
    <!--&lt;!&ndash; zookeeper连接地址 &ndash;&gt;-->
    <!--<constructor-arg value="127.0.0.1:2181"/>-->
    <!--</bean>-->
    <!-- 异步线程池 -->
    <bean id="aysncThreadPool" class="com.chenhaiyang.tcc.transaction.core.aysnc.ThreadPoolTransactionAysncExecutor">
        <!-- 线程池保持最小线程数，低于该值，线程不会回收 -->
        <constructor-arg index="0" value="100" />
        <!-- 线程池保持的最大线程数，大于上值，小于该值，线程回收 -->
        <constructor-arg index="1" value="250"/>
        <!-- 空闲线程空闲多久回收 -->
        <constructor-arg index="2" value="120"/>
    </bean>

    <!-- TCC事务配置器 -->
    <bean id="transactionConfiguration" class="com.chenhaiyang.tcc.transaction.core.configuration.TccTransactionConfiguration">
        <!-- 事务存储器 -->
        <property name="transactionStorage" ref="transactionStorage"/>
        <!-- 事务所属的业务域 -->
        <property name="domain" value="root"/>
        <!-- 主事务的try和confirm异步执行的时候需要的线程池，不异步执行可以不配置 -->
        <property name="transactionAysncExecutor" ref="aysncThreadPool"/>
    </bean>
</beans>
```

### 扩展点与默认配置

#### 事务序列化工具可选项：
基于hessian2:
```xml
    <!-- 事务序列化工具：基于hessian2 -->
    <bean id="hessianSeralizer" class="com.chenhaiyang.tcc.transaction.core.serializer.hessian.HessianSerializer"/>
```
基于jdk的序列化：
```xml
    <!-- 事务序列化工具：基于jdk的序列化方案 -->
    <bean id="hessianSeralizer" class="com.chenhaiyang.tcc.transaction.core.serializer.java.JavaSerializer"/>
```
基于kryo的序列化方案：
```xml
    <!-- 事务序列化工具：基于jdk的序列化方案 -->
    <bean id="hessianSeralizer" class="com.chenhaiyang.tcc.transaction.core.serializer.kryo.KryoSerializer"/>
```
强烈建议使用Hessian2的序列化方式，性能较好，但兼容性很高。
如果想自己提供序列化方案，则需要编写自己的序列化类，实现com.chenhaiyang.tcc.transaction.api.TransactionSerializer
例如：
```xml
    <!-- 事务序列化工具：基于自己的序列化实现： -->
    <bean id="hessianSeralizer" class="com.xx.xxxx.xxxx.xxxx"/>
```
注意：<br/>
（一）每个主事务和分支事务可以选择不同的序列化方案。但如果中途更换序列化实现方案。则历史数据都无法再正确处理。如果更换序列化方案，记得处理事务日志存储中的历史数据。<br/>
（二）事务查询面板只能连接一个数据源。如果多个事务参与者共享一个数据库，请保持序列化方式一致。<br/>

#### 事务存储可选项目：
需要自行依赖缺少的类库。
##### 基于jdbc：
```xml
    <!-- TCC 业务活动日志（事务日志）的jdbc数据源 -->
    <bean id="tccDataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="clone">
        <!-- 基本属性driverClassName、 url、user、password -->
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/tcc-transaction?useUnicode=true" />
        <property name="username" value="root" />
        <property name="password" value="root" />

        <!-- 配置初始化大小、最小、最大 -->
        <!-- 通常来说，只需要修改initialSize、minIdle、maxActive -->
        <!-- 初始化时建立物理连接的个数，缺省值为0 -->
        <property name="initialSize" value="5" />
        <!-- 最小连接池数量 -->
        <property name="minIdle" value="10" />
        <!-- 最大连接池数量，缺省值为8 -->
        <property name="maxActive" value="20" />

        <!-- 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 -->
        <property name="maxWait" value="1000" />
    </bean>

    <!-- 事务持久化介质,自选,这里以jdbc为例 -->
    <bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.jdbc.JdbcTransactionStorage">
        <!-- 序列化方式 -->
        <constructor-arg ref="hessianSeralizer"/>
        <!-- jdbc数据源 -->
        <property name="dataSource" ref="tccDataSource"/>
        <!-- 数据库表名后缀 -->
        <property name="tbSuffix" value="_root"/>
    </bean>
```
jdbc的transactionStorage中需要配置tbSuffix，即事务表的后缀。建议配置为业务标识项目。<br/>
例如，如果你配置为 _order。则该事务的事务日志会存在表tcc_transaction_order表中 <br/>
##### 基于redis：
```xml
<!-- 事务持久化介质，自选，这里以redis为例 -->
    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg value="127.0.0.1"/>
        <constructor-arg value="6379"/>
   </bean>
    <bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.redis.RedisTransactionStorage">-->
        <!-- 序列化方式 -->
        <constructor-arg ref="hessianSeralizer"/>
        <!--分布式锁超时时间（upadate原子性使用分布式锁实现）-->
        <property name="lockTimeout" value="2000"/>
        <!--jedis pool -->
        <property name="jedisPool" ref="jedisPool"/>
    </bean>
```
上面的的是简易的基于REDIS配置，你还可以配置JedisPoolConfig等。
##### 基于zookeeper：
```xml
    <!-- 事务持久化介质，自选，这里以zookeeper为例 -->
    <bean id="transactionStorage" class="com.chenhaiyang.tcc.transaction.core.storage.zookeeper.ZookeeperTransactionStorage">
        <!-- 序列化方式 -->
        <constructor-arg ref="hessianSeralizer"/>
        <!-- zookeeper连接地址-->
        <constructor-arg value="127.0.0.1:2181"/>
    </bean>
```
基于zookeeper的例子如上。
##### 自定义存储介质：
（1）编写存储类继承类com.chenhaiyang.tcc.transaction.api.TransactionStorage
（2）在tcc-transaction.xml中定义你的自定义存储。

##### 建议：
如果你需要保证你的TCC分布式事务高性能，可以使用redis作为存储介质。注意：为了使得redis事务日志不丢失，请开启redis的持久化。
另外，redis实例需要支持lua脚本。

#### 异步线程池
一个异步线程池的Demo:异步线程池是给主事务执行异步提交时使用的，非主事务可以不配置。
```xml
    <bean id="aysncThreadPool" class="com.chenhaiyang.tcc.transaction.core.aysnc.ThreadPoolTransactionAysncExecutor">
        <!-- 线程池保持最小线程数，低于该值，线程不会回收 -->
        <constructor-arg index="0" value="100" />
        <!-- 线程池保持的最大线程数，大于上值，小于该值，线程回收 -->
        <constructor-arg index="1" value="250"/>
        <!-- 空闲线程空闲多久回收，单位秒 -->
        <constructor-arg index="2" value="120"/>
    </bean>

```
#### 事务配置管理器transactionConfiguration
事务配置管理器持有其他的配置
```xml
    <!-- TCC事务配置器 -->
    <bean id="transactionConfiguration" class="com.chenhaiyang.tcc.transaction.core.configuration.TccTransactionConfiguration">
        <!-- 事务存储器 -->
        <property name="transactionStorage" ref="transactionStorage"/>
        <!-- 事务所属的业务域 -->
        <property name="domain" value="root"/>
        <!-- 主事务的confirm和cancel异步执行的时候需要的线程池，不异步执行可以不配置 -->
        <property name="transactionAysncExecutor" ref="aysncThreadPool"/>
    </bean>
```
关键配置解析：
##### domain 
    事务的业务域，必须配置。一个try-confirm-cancel单元配置一个业务域
##### transactionAysncExecutor 
    异步线程池引用，如果需要事务异步提交或者回滚
##### transactionStorage
    引用的事务数据源配置
    
#### 事务恢复配置

在上面的各个配置项中，发现缺少事务恢复配置。实际上不配置已有缺省配置。<br/>
事务恢复配置代表当事务执行时发生异常，在内部定时任务恢复的配置策略，配置方式如下：<br/>
```xml

    <!-- 事务恢复配置，可选，有默认 -->
    <bean id="recoverConfig" class="com.chenhaiyang.tcc.transaction.core.recover.DefaultTransactionRecoverConfig">
        <!-- 事务恢复任务的cron表达式，默认1分支执行一次 -->
        <property name="cronExpression" value="0 */1 * * * ?"/>
        <!-- 事务恢复最大的重试次数，默认重试20次，超过即不再重试 -->
        <property name="maxRetryCount" value="20"/>
        <!-- 事务重试超过五次，下次重试时间推迟的时间间隔，默认60秒，推迟重试算法：（retriesCount-5）*nextRetriesAddTime  -->
        <property name="nextRetriesAddTime" value="60"/>
        <!-- 事务恢复阀值，即恢复多少秒之前的事务，默认为120秒，即2分钟 -->
        <property name="recoverDuration" value="120"/>
    </bean>
    <!-- TCC事务配置器 -->
    <bean id="transactionConfiguration" class="com.chenhaiyang.tcc.transaction.core.configuration.TccTransactionConfiguration">
        ......
        <!-- 事务恢复配置，可选，不配置有默认值 -->
        <property name="recoverConfig" ref="recoverConfig"/>
    </bean>

```


