package ui.containers;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public abstract class StatusContainerBase {
    public int hourOfDay;
    public DayOfWeek dayOfWeek;

    public StatusContainerBase(int hourOfDay, DayOfWeek dayOfWeek) {
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
    }
}
