package dev.onload.rocketmq.consumer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-27 18:56
 * @description 消费者常量
 */
@AllArgsConstructor
public enum ConsumeEnum {
    /**
     * 并发消费
     */
    CONCURRENT(1, "CONCURRENT"),
    /**
     * 顺序消费
     */
    ORDER(2, "ORDER");

    @Getter
    private int code;
    @Getter
    private String type;

    public static ConsumeEnum getConsumeType(int code) {
        for (ConsumeEnum value : ConsumeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
