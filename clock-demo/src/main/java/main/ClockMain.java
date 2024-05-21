package main;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class ClockMain {

    public static void main(String[] args) {
//        var timeMachineClock = Clock.systemUTC();

//        var timeMachineClock = Clock.fixed(LocalDateTime.of(2024, 1, 1, 10, 0)
//                .toInstant(ZoneOffset.UTC), ZoneId.systemDefault());

//        var timeMachineClock = Clock.offset(Clock.systemDefaultZone(), Duration.of(5, ChronoUnit.MINUTES));

//        var timeMachineClock = Clock.tick();

        var timeMachine = new TimeMachine(Clock.fixed(LocalDateTime.of(2024, 1, 1, 10, 0)
                .toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));

        var now = LocalDateTime.now(timeMachine.getClock());

        System.out.println(now);

        timeMachine.setClock(Clock.fixed(LocalDateTime.of(2024, 1, 1, 11, 0)
                .toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));

        now = LocalDateTime.now(timeMachine.getClock());

        System.out.println(now);

        System.out.println(timeMachine.now());
    }
}
