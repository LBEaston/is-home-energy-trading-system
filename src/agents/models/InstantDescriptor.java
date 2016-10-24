package agents.models;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 24/10/2016.
 */
public class InstantDescriptor {
    public DayOfWeek dayOfWeek;
    public int hourOfDay;

    public InstantDescriptor(DayOfWeek dayOfWeek, int hourOfDay) {
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
    }
}
