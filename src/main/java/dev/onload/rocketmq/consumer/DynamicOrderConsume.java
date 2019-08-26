package dev.onload.rocketmq.consumer;

import dev.onload.rocketmq.consumer.config.RocketMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import dev.onload.rocketmq.consumer.config.MessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-15 19:59
 * @description 动态顺序消费
 */
@Slf4j
@Component
public class DynamicOrderConsume implements MessageListenerOrderly {

    @Autowired
    private RocketMQProperties rocketMQProperties;

    public void consume(DefaultMQPushConsumer consumer) {
        try {
            log.info("开始顺序消费,DynamicOrderConsume.consumer:{}", consumer);
            consumer.registerMessageListener(this);
            consumer.start();
        } catch (MQClientException e) {
            log.error("顺序消费失败,DynamicOrderConsume.consumer:{}", consumer);
            e.printStackTrace();
        }
    }

    /**
     * 顺序消费
     *
     * @param messages
     * @param consumeOrderlyContext
     * @return
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> messages, ConsumeOrderlyContext consumeOrderlyContext) {
        try {
            log.info("开始顺序消费");
            long start = System.currentTimeMillis();
            //防止空消费,如果为空立刻返回
            if (CollectionUtils.isEmpty(messages)) {
                log.info("顺序消费耗时:{}", System.currentTimeMillis() - start);
                return ConsumeOrderlyStatus.SUCCESS;
            }
            long offset = messages.get(0).getQueueOffset();
            String maxOffset =
                    messages.get(0).getProperty(MessageConst.PROPERTY_MAX_OFFSET);
            long diff = Long.parseLong(maxOffset) - offset;
            if (diff > rocketMQProperties.getMaxMsgHeapUpNum()) {
                // TODO 消息堆积情况的特殊处理 可以将这些消息放到另一个线程池中处理
                log.info("堆积消息条数:{}", diff);
                log.info("顺序消费耗时:{}", System.currentTimeMillis() - start);
                return ConsumeOrderlyStatus.SUCCESS;
            }
            for (MessageExt msg : messages) {

                String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
                MessageStore.put(msg.getTags(),messageBody);
                log.info("Message Consumer: Handle New Message: messageId:{}, topic:{}, tags:{}, keys:{}, messageBody:{}"
                        , msg.getMsgId(), msg.getTopic(), msg.getTags(), msg.getKeys(), messageBody);

            }
            log.info("顺序消费耗时:{}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("Consume Message Error!!,", e);
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
