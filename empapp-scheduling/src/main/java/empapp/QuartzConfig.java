package empapp;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail buildJobDetail() {
        JobDetail jobDetail = JobBuilder.newJob(EmployeeJob.class)
                .withIdentity(UUID.randomUUID().toString(), "employees-job")
                .withDescription("Print employees Job")
                //.usingJobData("key1", "value1")
                .storeDurably()
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger buildJobTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "employee-triggers")
                .withDescription("Print employees Trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?"))
                .build();
    }
}
