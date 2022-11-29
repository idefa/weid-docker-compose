
# Weidentity 一站式体验流程
版本说明：
* Fisco bcos版本 2.9.0
* Webase  1.5.4
* weid-build-tools  1.0.28
* weid-sample master

## 1.创建网络
```bash
docker network create -d bridge --subnet=172.25.0.0/16 --gateway=172.25.0.1 web_network
```
## 2.一键运行

```bash
docker-compose up -d
docker-compose ps
webase由于有依赖，如果没启动,重复执行 docker-compose up -d到全部启动为止
```



## 3.查看Webase

* Webase链管理工具 http://localhost:5000/

## 4. 配置Weidentity
* Weidentity管理工具 http://localhost:6021/
#### 创建Weidentity数据库
连接mysql,创建Weidentity的数据库，mysql连接是 127.0.0.1:23306  账号: root/123456 
#### Weidentity可视化安装
https://weidentity.readthedocs.io/zh_CN/release-1.8.5/docs/deploy-via-web.html



## 5. 访问Weid 示例

* WeId Sample:http://localhost:6101/swagger-ui.html

## 其他：重置所有数据,重新初始化Webase,WeIdentiy
```bash
./reset.sh
```




