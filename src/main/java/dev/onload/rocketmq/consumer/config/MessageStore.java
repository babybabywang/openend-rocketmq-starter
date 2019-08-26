package dev.onload.rocketmq.consumer.config;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-27 19:54
 * @description 消息存储中心
 */
public class MessageStore {
    private static final ConcurrentHashMap<String, List<String>> MESSAGE_STORE = new ConcurrentHashMap<>(2000);

    public static void put(String tag, String message) {
        //容量限制5000
        if (MESSAGE_STORE.size() > 5000) {
            //TODO 后期入redis或者mongo中
            return;
        }
        List<String> messageList = get(tag);
        if (CollectionUtils.isEmpty(messageList)) {
            messageList = Lists.newArrayList();
        }
        messageList.add(message);
        MESSAGE_STORE.put(tag, messageList);
    }

    public static List<String> get(String tag) {
        return MESSAGE_STORE.get(tag);
    }

    public static void remove(String tag) {
        MESSAGE_STORE.remove(tag);
    }
}
