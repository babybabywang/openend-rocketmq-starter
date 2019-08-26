package dev.onload.rocketmq.consumer.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * rocketmq配置
 *
 * @author echo huang
 */
@Data
@Component
public class RocketMQProperties {
    /**
     * 最大消息堆积数量,默认10w
     */
    private Long maxMsgHeapUpNum = 100000L;
}
