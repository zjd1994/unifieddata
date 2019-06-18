package com.bootdo.common.config;

import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.bootdo.common.scheduler.FtpDownloadJob;
@Configuration
public class FtpQuartzCOnfig {
	@Bean
	public FtpDownloadJob ftpDownloadJob() {
		return new FtpDownloadJob();
	}
	
	
	@Bean
	public MethodInvokingJobDetailFactoryBean downloadJobDetail() {
		MethodInvokingJobDetailFactoryBean bean=new MethodInvokingJobDetailFactoryBean();
		bean.setTargetObject(ftpDownloadJob());
		bean.setTargetMethod("execute");
		return bean;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean downloadJobDetail2() {
		MethodInvokingJobDetailFactoryBean bean=new MethodInvokingJobDetailFactoryBean();
		bean.setTargetObject(ftpDownloadJob());
		bean.setTargetMethod("execute2");
		return bean;
	}
	
	@Bean 
	public CronTriggerFactoryBean downloadTriggerFactoryBean() {
		CronTriggerFactoryBean bean=new CronTriggerFactoryBean();
		bean.setJobDetail(downloadJobDetail().getObject());
		bean.setCronExpression("30 * * * * ?");
	
		return bean;
	}
	
	@Bean("schedulerFactoryBean")
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean=new SchedulerFactoryBean();
//		schedulerFactoryBean.setTriggers(new Trigger[] {downloadTriggerFactoryBean().getObject()});
		//schedulerFactoryBean.setAutoStartup(false);
		return schedulerFactoryBean;
	}
	
}
