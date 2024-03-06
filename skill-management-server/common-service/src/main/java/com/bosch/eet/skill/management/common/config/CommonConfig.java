package com.bosch.eet.skill.management.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan({"com.bosch.eet.skill.management.common"})
@PropertySource(value = "classpath:application-common.properties")
public class CommonConfig {

}
