package org.spring.aicloud.entity.enums;

/**
 * Answer AiType 枚举: AI工具类型
 */
public enum AiTypeEnum {
    CHAT(1),
    DRAW(2);

    private int value;

    AiTypeEnum(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        System.out.println(AiTypeEnum.CHAT.value);
    }
    public int getValue() {
        return value;
    }
}
