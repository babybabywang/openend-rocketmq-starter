package dev.onload.rocketmq.consumer.service;

import dev.onload.rocketmq.consumer.config.ConsumeConfig;

import java.util.List;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-27 18:47
 * @description 外部扩展消息
 */
public interface HandleService {

    /**
     * 处理消费
     *
     * @param consumeConfigList
     */
    void handleConsume(List<ConsumeConfig> consumeConfigList);

    /**
     * 处理消息
     *
     * @param message
     */
    default String handleMessage(String message) {
        return message;
    }
}
