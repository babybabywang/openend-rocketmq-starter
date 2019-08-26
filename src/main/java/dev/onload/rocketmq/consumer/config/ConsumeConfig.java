package dev.onload.rocketmq.consumer.config;

import lombok.Builder;
import lombok.Data;

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
