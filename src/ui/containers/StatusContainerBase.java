package ui.containers;

import java.time.DayOfWeek;

/**
 * Created by Aswin Lakshman on 13/10/2016.
 */
public abstract class StatusContainerBase {
    public int weeksElapsed;
    public int hourOfDay;
    public DayOfWeek dayOfWeek;

    public StatusContainerBase(int hourOfDay, DayOfWeek dayOfWeek) {
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
    }

    public StatusContainerBase(int hourOfDay, DayOfWeek dayOfWeek, int weeksElapsed) {
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
        this.weeksElapsed = weeksElapsed;
    }
}
