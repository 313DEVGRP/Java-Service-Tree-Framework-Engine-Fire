package com.arms.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.arms.utils.external_communicate")
public class OpenFeignConfig {
}
