class: inverse, center, middle

# Ütemezés Quartz használatával

---

## Quartz

* Ütemezés nagyvállalati környezetben
* Perzisztencia adatbázisban
* JTA tranzakciókezelés
* Clustering (failover, load balancing)
* Naptárkezelés, szabadnapok
* Job listener komplex workflow létrehozására
* Plugins, pl. history-ra
* Trigger prioritás
* Terracotta perzisztencia

---

## Quartz függőség

* A `pom.xml` fájlban:

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

---

## Job

```java
@AllArgsConstructor
public class EmployeeJob extends QuartzJobBean {

    private EmployeeService employeeService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<EmployeeDto> employees = employeeService.listEmployees();
        // String value = context.getMergedJobDataMap().getString("key1");
        System.out.println("Running cron job, employees " + employees.size());
    }
}
```

* Működik a Dependency Injection

---

## JobDetail

```java
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
```

---

## Trigger

```java
@Bean
public Trigger buildJobTrigger(JobDetail jobDetail) {
    return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("PrintEmployeesCountJob")
            .withDescription("Print employees Trigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?"))
            .build();
}
```

* Vagy `SimpleScheduleBuilder`

---

class: inverse, center, middle

# Quartz adatbázis perzisztencia

---

## Adatbázis perzisztencia

* Az `application.properties` fájlban

.small-code-14[
```properties
spring.quartz.job-store-type=jdbc
spring.quartz.properties.org.quartz.jobStore.class=org.springframework.scheduling.quartz.LocalDataSourceJobStore
```
]

* SQL fájlok a `\org\quartz\impl\jdbcjobstore` könyvtárban
    * Flyway vagy Liquibase
* Quartz maga inicializálja

```properties
spring.quartz.jdbc.initialize-schema=always
```

* Postgres esetén

.small-code-14[
```properties
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.PostgreSQLDelegate
```
]

---

## Adatbázis táblák

<img src="images/quartz-tables.png" alt="Quartz táblák" width="500" />

---

## Adatbázis táblák leírása

* `qrtz_job_details` - job adatai
* `qrtz_triggers` - trigger adatai
* `qrtz_simple_triggers`, `qrtz_simprop_triggers`, `qrtz_crons_triggers`, `qrtz_blob_triggers` - megfelelő típusú triggerek
  specifikus adatai, külső kulccsal hivatkoznak a `qrtz_triggers` táblára
* `qrtz_fired_triggers` - elindított triggerek adatai
* `qrtz_paused_trigger_grps` - nem aktív triggerek adatai
* `qrtz_calendars` - ünnepnapok tárolására
* `qrtz_lock` - clusteres működés esetén erre történik a lockolás, hogy egyszerre csak egy node-on fusson
* `qrtz_scheduler_state` - clusteres működés esetén ide kerül tárolásra a node-ok adatai, hogy az aktív node kiesése esetén át tudja venni egy passzív node

---

class: inverse, center, middle

# Quartz clusterezés

---

## Clusteres környezetben

* Csak egy node futtassa a jobot
* Ugyanaz a `JobDetail` identity

```java
JobDetail jobDetail = JobBuilder.newJob(EmployeeJob.class)
                .withIdentity("PrintEmployeesCountJob")
```

* `application.properties` változtatások

.small-code-14[
```properties
spring.quartz.properties.org.quartz.jobStore.class=org.springframework.scheduling.quartz.LocalDataSourceJobStore
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=5000
```
]
