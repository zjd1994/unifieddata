package com.bootdo.test;

import javax.annotation.Resource;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/quartz")
public class TestController {
	@Autowired
	@Qualifier("schedulerFactoryBean")
	Scheduler scheduler;
	
	@Resource(name = "downloadTriggerFactoryBean")  
    private CronTrigger cronTrigger;
//	SchedulerFactoryBean bean = ApplicationContextRegister.getBean(SchedulerFactoryBean.class);
//	MethodInvokingJobDetailFactoryBean downloadJobDetail=ApplicationContextRegister.getBean(MethodInvokingJobDetailFactoryBean.class);
	@RequestMapping("/start")
	public String start() throws SchedulerException {
//		CronTriggerFactoryBean cronBean=new CronTriggerFactoryBean();
//		cronBean.setJobDetail(downloadJobDetail.getObject());
//		cronBean.setCronExpression("0 * * * * ?");
//		bean.setTriggers(new Trigger[] {cronBean.getObject()});
//		bean.start();
//		System.err.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//		return "test";
		System.out.println( scheduler );
		 
        JobKey key = new JobKey("downloadJobDetail");
        scheduler.resumeJob(key);
 
        return "start";
		
	}
	
	@RequestMapping("/stop")
	public String stop() throws SchedulerException {
//		bean.stop();
//		bean
//		System.err.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//		return "test";
		JobKey key=new JobKey("downloadJobDetail");
		scheduler.pauseJob(key);
		return "stop";
	}
	
    @RequestMapping("/trigger")
    public String trigger(@RequestParam String cronExpression) throws Exception {
        // 获取任务
//        JobKey jobKey = new JobKey("downloadJobDetail");
//        // 获取 jobDetail
//        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//        JobKey jobKey2 = new JobKey("downloadJobDetail2");
//        JobDetail jobDetail2 = scheduler.getJobDetail(jobKey2);
        
        //CronTrigger trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());  
        
        
//        // 生成 trigger
//        Trigger trigger = TriggerBuilder
//                .newTrigger()
//                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
//                .build();
//        // 删除任务，不删除会报错。报任务已存在
//        scheduler.deleteJob(jobKey);
//        // 启动任务
//        scheduler.scheduleJob(jobDetail, trigger);
        
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());  
        String currentCron = trigger.getCronExpression();// 当前Trigger使用的  
//        System.err.println(trigger.getCronExpression());
        String searchCron =cronExpression;// 从数据库查询出来的  
        System.out.println(currentCron);  
        System.out.println(searchCron);  
        if (currentCron.equals(searchCron)) {  
            // 如果当前使用的cron表达式和从数据库中查询出来的cron表达式一致，则不刷新任务  
        } else {  
            // 表达式调度构建器  
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(searchCron);  
            // 按新的cronExpression表达式重新构建trigger  
            trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());  
            trigger = trigger.getTriggerBuilder().withIdentity(cronTrigger.getKey())  
                    .withSchedule(scheduleBuilder).build();  
            // 按新的trigger重新设置job执行  
            scheduler.rescheduleJob(cronTrigger.getKey(), trigger);  
            currentCron = searchCron;  
        }  
        return "trigger";
    }

}
