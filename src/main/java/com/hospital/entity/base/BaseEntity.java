package com.hospital.entity.base;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类，所有实体类需继承
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID (使用 Snowflake 分布式 ID)
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}