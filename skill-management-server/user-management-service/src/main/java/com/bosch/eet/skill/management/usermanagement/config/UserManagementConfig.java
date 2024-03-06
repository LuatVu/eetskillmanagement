package com.bosch.eet.skill.management.usermanagement.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@ComponentScan({"com.bosch.eet.skill.management.usermanagement"})
@PropertySource(value = "classpath:application-usrm.properties")
public class UserManagementConfig {

    @Bean("CoreUserNamagementMessageSource")
    public MessageSource messageSource() {
        Locale.setDefault(Locale.ENGLISH);
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
