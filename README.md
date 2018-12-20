rpc-tcc-transaction
=======================================================================
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
### 分布式事务tcc方案开源框架。基于java语言来开发（JDK1.8）,不与具体的RPC框架耦合

## 框架包结构简介

#### rpc-tcc-transaction-dmoain

    框架的公共领域模型
    
#### rpc-tcc-transaction-api

    框架的核心组件关键API定义和内部实现核心领域模型定义
    
#### rpc-tcc-transaction-core

    框架的核心组件关键API及其实现
    
#### rpc-tcc-transaction-spring

    框架在spring技术栈内的插桩代码实现。利用了Spring的AOP原理。
    Spring项目只需要整合该工程即可实现tcc-分布式事务。
    
#### rpc-tcc-transaction-dashboard

    可单独执行的分布式事务查看面板，可以重置分布式事务的重试次数以及删除历史的错误的分布式事务日志。
    
#### rpc-tcc-transaction-example
     
    tcc分布式事务基于dubbo框架的Demo示例工程。内置了：
    
##### rpc-tcc-transaction-example-branchservice-api

    Demo服务分支事务的服务提供者服务API
    
##### rpc-tcc-transaction-example-branchservice

    Demo服务分支事务的服务提供者实现
##### rpc-tcc-transaction-example-rootservice

    Demo服务主事务的服务实现。主事务通过分支事务API调用分支事务的具体实现。
    
## 框架的基本原理

    框架有两个角色。主事务和分支事务。其中：
    主事务：主动调用方。
    分支事务：被调用方。
    其中，一个主事务可以包含多个分支事务。
    
    在远程调用过程中，框架会搜集事务的参与者并生成事务日志。
    分支事务和主事务都是事务参与者。
    每一个事务参与者都要求包含三个函数：
    tryXXX() confirmXXX() cancelXXX()
    
    接口被调用，事务框架先会调用每一个事务参与者的try方法。
    try阶段如果没有异常，则调用confirm方法。否则，则调用cancel方法。
    tcc分布式事务要求业务方要保证三个接口的幂等。
    
    框架内置了一个定时线程会定时查询没有成功的事务进行事务恢复。
    如果事务日志记录的阶段是confirm阶段，则会不断补偿调用每个事务参与者的confrim方法，直到超过最大重试次数。
    否则，则调用每一个事务参与者的cancel方法，直到超过最大重试次数。
    
    注意：只有主事务才可以发起事务恢复，分支事务等待主事务发起事务恢复时调用。
    
    可以在dashboard面板查看超时的事务日志做相应的决策：删除或者重置重试点。
    
## 框架的使用方法。以example为例 

一，分支事务rpc的API接口类要依赖本框架的domain包

```xml
        <!-- API要依赖分布式事务的domain包-->
        <dependency>
            <artifactId>rpc-tcc-transaction-domain</artifactId>
            <groupId>com.github.chenhaiyangs</groupId>
            <version>1.0.0</version>
        </dependency>
```
在API的定义里需要保证一个入参为TransactionContext,如：
在远程方法的定义里，允许只暴露出去try方法。confirm方法和cancel方法可以不暴露。
```java
   /*
    *  分支事务的try方法暴露远程 
    */
    Response branchTryMethod(TransactionContext transactionContext, BusinessRequest request);
```
二，分支事务rpc的实现工程要依赖Spring包
```xml
        <!-- 分布式事务在Spring工程中的核心实现-->
        <dependency>
            <groupId>com.github.chenhaiyangs</groupId>
            <artifactId>rpc-tcc-transaction-spring</artifactId>
            <version>1.1.0</version>
        </dependency>
```
三,分支事务角色的RPC接口实现的try方法需要添加@TccTransaction注解，指定confirm和cancel方法的函数名称。三个函数入参必须完全一样。
```java
    /**
     *  try方法需要添加TccTransaction 指定confirm方法和cancel方法
     */
     @TccTransaction(confirmMethod = "branchconfirmMethod",cancelMethod = "branchcancelMethod")
     @Override
     public Response branchTryMethod(TransactionContext transactionContext, BusinessRequest request) {
         ......
     }
    /**
     * confirm函数
     */
     public Response branchconfirmMethod(TransactionContext transactionContext, BusinessRequest request) {
         ......
     }
     /**
      * cancel函数
      */
      public Response branchcancelMethod(TransactionContext transactionContext, BusinessRequest request) {
          ......
      }
```
三,分支事务的工程需要加载tcc-transaction.xml文件。文件配置Demo详见example的branchservice的config文件夹。
    
    tcc-transaction.xml文件配置详解后续解释
   
四，主事务实现工程需要依赖jar包：
```xml
        <!-- 分布式事务在Spring工程中的核心实现-->
        <dependency>
            <groupId>com.github.chenhaiyangs</groupId>
            <artifactId>rpc-tcc-transaction-spring</artifactId>
            <version>1.1.0</version>
        </dependency>
```
五,主事务角色的业务实现的try方法需要添加@TccTransaction注解，指定confirm和cancel方法的函数名称。三个函数入参必须完全一样。也可以指定是否异步执行comfirm或者cancel方法。

主事务的接口入参定义不要求有transactionContext参数。
```java
    @TccTransaction(confirmMethod = "rootconfirmMethod",cancelMethod = "rootcancelMethod",async = true)
    public String rootTryMethod(RootRequest msg){
        ......
        //调用远程rpc框架的try接口
        Response response = branchExampleApi.branchTryMethod(null,request);
        
    }
   /**
    * confirm函数
    */
    public String rootconfirmMethod(RootRequest msg){
        ......
    }
   /**
    * confirm函数
    */
    public String rootcancelMethod(RootRequest msg){
        ......
    }
```
六,主事务的工程需要加载tcc-transaction.xml文件。文件配置Demo详见example的rootservice的config文件夹。

    tcc-transaction.xml文件配置详解后续解释
    
## [tcc-transaction.xml的具体可配置项与可扩展点](./doc/transactionxml.md)
## [控制管理面板](./doc/dashborad.md)
## FAQ
#### 为什么我下载代码以后，没有找到get/set／toString 方法
因为框架使用了Lombok包，它是在编译的时期，自动生成get／set／toString等方法，并不影响运行，如果觉得提示错误难受，请自行下载lombok包插件。

#### 为什么启动以后，执行分支事务报错找不到方法
请配置proxy-target-class="true"
```xml
    <aop:aspectj-autoproxy proxy-target-class="true"/>
```
上面说过，RPC的API可以只暴露try方法，cancel和confirm方法可以只在实现类里添加。proxy-target-class="true"表示使用cglib动态代理。
如果不配置proxy-target-class="true"，则动态代理使用jdk的动态代理，直接找PRC的API定义接口，由于API定义接口中没有定义cancel和confirm方法，因此会导致实际执行时发生错误！

