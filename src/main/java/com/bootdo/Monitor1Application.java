package com.bootdo;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.MultipartConfigElement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.bootdo.common.utils.SpringContextUtil;


@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableTransactionManagement
@ServletComponentScan
@MapperScan("com.bootdo.*.dao")
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class Monitor1Application {

	public static void main(String[] args) {
		SpringApplication.run(Monitor1Application.class, args);
		/*TimerTask timerTask = new  KafkaCacheUtils();
        Timer t=new Timer(false);
        t.schedule(timerTask, 3000,300000);*/
	}
	
	 /**
     *
     * */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory config = new MultipartConfigFactory();
        config.setMaxFileSize("9000MB");
        config.setMaxRequestSize("9000MB");
        return config.createMultipartConfig();
    }
    @Bean
    public Object getBean() {
    	SpringContextUtil config = new SpringContextUtil();
        return config;
    }
    
	/*
	 * @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
	 */
}
