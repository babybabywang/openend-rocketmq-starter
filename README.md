# 基于RocketMQ的ConsumerGroup的消息动态消费

## 配置类详解
```java  
  
/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-15 19:24
 * @description 消费者配置
 */
@Data
@Builder
public class ConsumeConfig {

    /**
     * 消费者类型 1.并发消费 2.顺序消费
     */
    private Integer consumeType;
    /**
     * topic
     */
    private String topic;
    /**
     * 订阅的表名,监听多个表用||区分
     */
    private String tableName;
    /**
     * 消费组
     */
    private String consumerGroup;
    /**
     * nameServerAddr
     * <p>格式为:ip1:port1;ip2:port2</p>
     */
    private String nameServerAddr;

    /**
     * 最大消费线程,默认64
     */
    private Integer consumeThreadMax = 64;

    /**
     * 最小消费线程,默认20
     */
    private Integer consumeThreadMin = 20;
    /**
     * 一个queue最大消费的消息个数，用于流控,默认1000
     */
    private Integer pullThresholdForQueue = 1000;

    /**
     * 消息一次拉取的量,默认32
     */
    private Integer pullBatchSize = 32;
}

```
>这是一个消费者配置类,这些配置可以基于使用者动态提供,可以做到不懂消费组中对于多个tag的动态配置

## 使用方式

### pom依赖
```xml
 <dependency>
     <groupId>dev.onload.rocketmq</groupId>
     <artifactId>openend-rocketmq-starter</artifactId>
     <version>1.0-SNAPSHOT</version>
 </dependency>
```

### 注入HandleServiceImpl实现类
```java

@Resource
private HandleServiceImpl handleServiceImpl;
    
```

### 简单demo使用技巧
```java
package dev.study.rocketmq;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.mallcai.rocketmq.consumer.HandleServiceImpl;
import org.mallcai.rocketmq.consumer.config.ConsumeConfig;
import org.mallcai.rocketmq.consumer.config.MessageStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-27 19:21
 * @description
 */
@SpringBootApplication(scanBasePackageClasses = HandleServiceImpl.class)
@RestController
@Slf4j
public class TestOpenEnd {
    @Resource
    private HandleServiceImpl handleServiceImpl;

    @GetMapping("startConsume")
    public void test() {
        List<ConsumeConfig> list = Lists.newArrayList();
        ConsumeConfig build = ConsumeConfig.builder().consumerGroup("test-group")
                .nameServerAddr("localhost:9876")
                .topic("TopicTest")
                .consumeType(1)
                .tableName("TagA")
                .consumeThreadMax(40)
                .consumeThreadMin(20)
                .pullBatchSize(20)
                .pullThresholdForQueue(2000)
                .build();
        list.add(build);
        handleServiceImpl.handleConsume(list);
    }

    @GetMapping("getMsg")
    public List<String> getMsg() {
        List<String> strings = null;
        synchronized (this) {
            strings = MessageStore.get("TagA");
            MessageStore.remove("TagA");
        }
        log.error("xxx");
        return strings;
    }
}

```

## 不足之处
> 首先为什么想到这个方案,因为项目中遇到单机因为业务原因造成被流控,因此我想到类基于tag来做到动态的消息分发,但是同一个ConsumerGroup不是能够消费不同的tag,因此我基于SpringBoot的SPI机制开发来这个启动类,
想法是用于压榨单机的消费能力。但是这样带来的问题造成broker的tps会逐渐降低,导致rocketMQ的服务器出现瓶颈,因此在项目中我并未使用这一方案,而是基于动态线程池来解决这个问题。

> 对于这个项目的问题,不光是多消费组造成broker的tps下降的问题,同时对于消息的存储,目前放在ConcurrentHashMap中,也就是说完全放在JVM内存中,不过这里我对Map的容量做来限制,后期也可以基于这点将超过限制的数据放到mysql或者redis中,
对于这个项目还是存在一些问题的,不过也是自己的一时想法,仔细琢磨后这样来处理客户端消费能力低来说还是存在一些问题,写出来也是为了将想法实践,做了永远都没有错。