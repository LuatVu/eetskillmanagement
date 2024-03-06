package com.bosch.eet.skill.management.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryBuilder {
	
	private String query;
	
	private Integer size;

	private Integer from;
	
}