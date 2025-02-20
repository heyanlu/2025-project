# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/index.html#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)


## RocketMQ
Enter CMD go to Folder D:\rocketmq-all-4.9.3-bin-release\rocketmq-4.9.3\bin
Type
`start mqnamesrv`
Launch a new terminal with:
`start mqbroker -n localhost:9876 autoCreateTopic=true`
Delete Message:
`./mqadmin deleteTopic -c DefaultCluster -n localhost:9876 -t {topicName}`
Check Message:
`mqadmin consumerProgress -g consumerGroup -n localhost:9876`
`mqadmin topicStatus -n localhost:9876 -t consumerTopic`
### Check Port:
netstat -ano | findstr "9876"
### ShutDown RocketMQ
mqshutdown broker
mqshutdown namesrv
如果不是第一次运行rocketmq，则将C:\Users\Administrator\store文件夹下的文件全部删除，则可以正常启动
### Delete Message:
`./mqadmin deleteTopic -c DefaultCluster -n localhost:9876 -t {topicName}`
### Environment setup
Key: ROCKETMQ_HOME
Val: D:\rocketmq-all-4.9.3-bin-release\rocketmq-4.9.3
### Elasticsearch
Enter CMD go to Folder D:\elasticsearch-7.4.2\bin
type
`elasticsearch`

Enter CMD goto folder D:\kibana-7.4.2-windows-x86_64\kibana-7.4.2-windows-x86_64\bin
type
`kibana.bat`
visit http://127.0.0.1:5601/


### Install Jar
https://maven.apache.org/download.cgi
Update POM with pacakage Jar and plugins for maven
In Plugins do compile and then jar
Copy from target to root path, then type:
```
mvn install:install-file -Dfile=target/OnlineShopping_03-2.0.jar -DgroupId=com.qiuzhitech -DartifactId=OnlineShopping_03 -Dversion=2.0 -Dpackaging=jar
```
### CONSUL
Windows下执行：
consul.exe agent -dev
Mac下执行：
./consul agent -dev


# MYSQL new user
CREATE USER 'rootroot'@'localhost' IDENTIFIED BY 'rootroot';
GRANT ALL PRIVILEGES ON *.* TO 'rootroot'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;

### Upload to EC2
```
scp -i "ec2key.pem" OnlineShopping_04-0.0.1-SNAPSHOT.jar ubuntu@ec2-18-218-151-35.us-east-2.compute.amazonaws.com:/home/ubuntu
```

## Docker
Install Docker  https://docs.docker.com/desktop/install/mac-install/
docker build -t com.qiuzhitech/onlinehopping04 .
docker run -p 8080:8080 onlineshopping_04-app

## Docker compose
docker-compose build
docker-compose up
docker-compose --version

# update permission 
sudo vi /etc/redis/redis.conf
   bind = 0.0.0.0
sudo vi /etc/mysql/my.cnf 
   bind-address = 0.0.0.0
## Enter docker Debug
docker ps
docker exec -it 08fcc134c543 /bin/sh
docker exec -it 08fcc134c543 mysql -u root -p

# Copy volume to server
# WINDOWS
docker run --rm -v onlineshopping_04_mysql_data:/data -v ${PWD}:/backup alpine sh -c "tar czf /backup/mysql_data_backup.tar.gz -C /data ."
# MAC
docker run --rm -v mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_data_backup.tar.gz -C /data .
docker volume create mysql_data
docker run --rm -v mysql_data:/data -v ./:/backup alpine sh -c "tar xzf /backup/mysql_data_backup.tar.gz -C /data"


# Run docker
docker image ls
docker run -d --name onlineshopping_04-mysql-1 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=online_shopping -e MYSQL_USER=rootroot -e MYSQL_PASSWORD=rootroot -p 3306:3306 mysql_image:latest

docker run -p 6379:6379 redis:latest &
docker run -p 8080:8080 onlineshopping_04-app &
docker run --name onlineshopping_app \
--link onlineshopping_mysql:mysql \
--link onlineshopping_redis:redis \
-p 8080:8080 \
-d onlineshopping_app


##Optional Docker learning:
## Commit docker to image
docker exec <container_id> cp -r /var/lib/mysql /data_inside_container/
docker exec e49affc71722 cp -r /var/lib/mysql /data_inside_container/

docker commit <container_id_or_name> <new_image_name>
docker commit e49affc71722 mysql_image

## Save docker
docker save -o onlineshopping_04-app.tar onlineshopping_04-app
docker save -o mysql_image.tar mysql_image
docker save -o redis_image.tar redis_image

## Load docker
docker image ls
sudo docker load -i onlineshopping_04-app.tar
sudo docker load -i mysql_image.tar
sudo docker load -i redis_image.tar

## docker network
docker network create my-network
docker run --name mysql-container --network my-network -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
docker run -p 8080:8080  --network my-network com.qiuzhitech/onlinehopping04

## Docker delete container
docker ps -a
docker stop 11818a989ca1
docker remove 11818a989ca1

## Docker delete images
docker images
docker rmi ae60b6bc2310
docker rmi redis