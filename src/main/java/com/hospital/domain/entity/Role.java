package com.hospital.domain.entity;

import com.hospital.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: JamHoo
 * @Description: 角色实体类
 * @Date: 2026/3/16 21:13
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Role extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 角色名
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;

	/**
	 * 角色排序
	 */
	private Integer roleSort;

	/**
	 * 角色描述
	 */
	private String roleDesc;
}
