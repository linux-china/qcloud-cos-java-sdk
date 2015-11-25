qcloud-cos-java-sdk
=================================
Java sdk for [腾讯云对象存储服务](http://wiki.qcloud.com/wiki/COS%E4%BA%A7%E5%93%81%E4%BB%8B%E7%BB%8D)

### 如何使用

* 在pom.xml添加以下依赖: 
      
        <dependency>
            <groupId>com.qcloud</groupId>
            <artifactId>cos-sdk</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>

* 创建CosCloud类实例,然后基于BucketOperation进行相关的操作

### 结构设计
COS SDK整体结构如下: 

![COS SDK类图](https://github.com/linux-china/qcloud-cos-java-sdk/blob/master/src/main/uml/CosSDK-class-diagram.png?raw=true)

主要的入口是CosClient,负责创建基于Bucket的操作,最终有BucketOperationImpl负责和底层的CosRequest完成和COS服务端的通讯请求,获取最终的CosResponse响应结果.

### 参考

* COS产品介绍:  http://www.qcloud.com/wiki/COS%E4%BA%A7%E5%93%81%E4%BB%8B%E7%BB%8D