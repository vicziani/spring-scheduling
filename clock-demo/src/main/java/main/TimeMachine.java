package main;

import java.time.Clock;
import java.time.LocalDateTime;

public class TimeMachine {

    private Clock clock;

    public TimeMachine(Clock clock) {
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
