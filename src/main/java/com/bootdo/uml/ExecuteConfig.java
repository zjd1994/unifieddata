package com.bootdo.uml;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecuteConfig {

    @Bean("executer")
    public Executor getExecutor(){
        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);

        executor.setMaxPoolSize(80);

        executor.setQueueCapacity(2000);

        executor.setKeepAliveSeconds(2);

        executor.initialize();

        return executor;
    }
}
