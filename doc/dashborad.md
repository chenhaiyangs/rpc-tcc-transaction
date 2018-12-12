## dashboard 启动步骤：
一，执行命令：git clone https://github.com/chenhaiyangs/rpc-tcc-transaction.git <br/>
二，cd rpc-tcc-transaction-dashboard/ <br/>
三，执行命令：mvn clean package，生成一个zip <br/>
四，解压缩zip，在application.yml配置启动端口和用户名密码，在transaction-datasource.xml配置数据源和序列化方式<br/>
五，执行命令：java -jar transaction-dashborad.jar 启动控制面板，自行编写脚本后台启动
