package com.bosch.eet.skill.management.common.dto;

import java.io.Serializable;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.Data;

/**
 * Generic DTO class to unify generic methods on different DTOs
 * @author GNN7HC
 *
 */
@Data
public abstract class GenericDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	public String toJson() {
		return JsonUtils.convertToString(this);
	}
}
