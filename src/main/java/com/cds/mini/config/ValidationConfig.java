package com.cds.mini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * This class is for bean validation.
 */
@Configuration
public class ValidationConfig {

    @Bean(name = "beanValidator")
    public Validator validatorFactory() {
        return new LocalValidatorFactoryBean();
    }
}
