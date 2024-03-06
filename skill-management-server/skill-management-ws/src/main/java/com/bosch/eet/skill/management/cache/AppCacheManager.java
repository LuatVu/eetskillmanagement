/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */
//There is two ways to evict a cache, either by using the @CacheEvict annotation on a method 
//or by auto-wiring the CacheManger and clearing it by calling the clear() method.
@Slf4j
@Component
public class AppCacheManager {

	@Autowired
	private CacheManager cacheManager;
	
	public void clearAllCache() {
		for (String cacheName : cacheManager.getCacheNames()) {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null) {
				cache.clear(); // clear cache by key/name 
			}
		}
	}

	public void clearCache(final String cacheName) {
		if (!StringUtils.isEmpty(cacheName)) {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null) {
				cache.clear(); // clear cache by key/name
			}
		}
	}
	
	public void clearSingleCacheValue(final String cacheName, final String cacheKey) {
	    cacheManager.getCache(cacheName).evict(cacheKey);
	}	

	/**
	 * The pattern is: second, minute, hour, day, month, weekday
	 * 0 0/30 * * * ? means that execute after every 30 min.
	 * 0 0 22 * * MON-FRI means that execute on the 10 PM weekdays
	 *   
	 * */
//	@Scheduled(cron = "0 0 22 * * *") 
	public void clearCacheSchedule() {
		clearAllCache();
	}

	public Cache getCache(final String cacheName) {
		return cacheManager.getCache(cacheName);
	}
	
	@CacheEvict(cacheNames = Constants.CREDENTIALS_CACHE_NAME, allEntries = true)
	public void evictAllCredentialsCacheValues() {
		log.info("All Credentials Cache is cleared");
	}	
	
	@CacheEvict(cacheNames = Constants.CREDENTIALS_CACHE_NAME, key = "#cacheKey")
	public void evictSingleCredentialsCacheValue(final String cacheKey) {
		log.info("Credentials Cache with " + cacheKey + " key is cleared");
	}
	
}
