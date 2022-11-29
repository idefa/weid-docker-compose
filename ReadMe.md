
# Weidentity 一站式体验流程

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



## 3.查看

* Webase链管理工具 http://localhost:5000/ 账号 admin/Simmed1234
* Weidentity管理工具 http://localhost:6021/
* WeId Sample:http://localhost:6101/swagger-ui.html

## 4.重置数据,重新初始化Webase,WeIdentiy
```bash
./reset.sh
```




