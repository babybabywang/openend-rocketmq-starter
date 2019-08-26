package dev.onload.rocketmq.consumer;

import com.google.common.base.Preconditions;
import dev.onload.rocketmq.consumer.config.ConsumeConfig;
import dev.onload.rocketmq.consumer.enums.ConsumeEnum;
import dev.onload.rocketmq.consumer.service.HandleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-15 19:15
 * @description 动态消费抽象类
 */
@Slf4j
public class HandleServiceImpl implements HandleService {

    @Autowired
    private DynamicOrderConsume dynamicOrderConsume;

    @Autowired
    private DynamicConcurrentlyConsume dynamicConcurrentlyConsume;

    @Override
    public void handleConsume(List<ConsumeConfig> consumeConfigList) {
        if (CollectionUtils.isEmpty(consumeConfigList)) {
            throw new RuntimeException("consumeConfigList不能为空");
        }
        consumeConfigList.forEach(consumeConfig -> {
            ConsumeEnum consumeType = ConsumeEnum.getConsumeType(consumeConfig.getConsumeType());
            Preconditions.checkNotNull(consumeType, "消费者类型不能为空");
            switch (consumeType) {
                case ORDER:
                    try {
                        dynamicOrderConsume.consume(startConsumer(consumeConfig));
                    } catch (MQClientException e) {
                        log.error("顺序消费订阅失败,dynamicOrderConsume.consumeConfig:{}", consumeConfig);
                    }
                    break;
                case CONCURRENT:
                    try {
                        dynamicConcurrentlyConsume.consume(startConsumer(consumeConfig));
                    } catch (MQClientException e) {
                        log.error("并发消费订阅失败,dynamicConcurrentlyConsume.consumeConfig:{}", consumeConfig);
                    }
                    break;
                default:
                    throw new RuntimeException("消费类型不匹配");
            }
        });
    }


    /**
     * 开启消费者
     *
     * @param consumeConfig
     * @return
     * @throws MQClientException
     */
    private DefaultMQPushConsumer startConsumer(ConsumeConfig consumeConfig) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumeConfig.getConsumerGroup());
        consumer.setNamesrvAddr(consumeConfig.getNameServerAddr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumeThreadMax(consumeConfig.getConsumeThreadMax());
        consumer.setConsumeThreadMin(consumeConfig.getConsumeThreadMin());
        consumer.setPullThresholdForQueue(consumeConfig.getPullThresholdForQueue());
        consumer.setPullBatchSize(consumeConfig.getPullBatchSize());
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(consumeConfig.getTopic(), consumeConfig.getTableName());
        return consumer;
    }

}
