package org.spring.aicloud.entity.enums;

/**
 * Answer AiModel 枚举: AI大模型枚举类
 */
public enum AiModelEnum {
    OPENAI(1),
    TONGYI(2),
    XUNFEI(3),
    WENXIN(4),
    DOUBAO(5),
    LOCAL(6);

    private int value;
    AiModelEnum(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        System.out.println(AiModelEnum.TONGYI.value);
    }
    public int getValue() {
        return value;
    }
}
