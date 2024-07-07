package org.spring.aicloud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  大模型查询结果
 * </p>
 *
 * @author li
 * @since 2024-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "aid", type = IdType.AUTO)
    private Long aid;

    /**
     * 问题
     */
    private String title;

    /**
     * 答案
     */
    private String content;

    /**
     * 大模型类型
     */
    private Integer model;

    private LocalDateTime createtime;

    private LocalDateTime updatetime;

    private Long uid;

    /**
     * 生成类型
     */
    private Integer type;


}
