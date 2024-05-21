package empapp;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class EmployeeJob extends QuartzJobBean {

    private EmployeeService employeeService;

    public EmployeeJob(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<EmployeeDto> employees = employeeService.listEmployees();
        context.getMergedJobDataMap().getString("key1");
        System.out.println("Running cron job, employees " + employees.size());
    }
}
