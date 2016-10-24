package ui.containers;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class ApplianceStatusContainer extends StatusContainerBase {
    public double consuming;

    public ApplianceStatusContainer(double consuming, int hourOfDay, DayOfWeek dayOfWeek) {
        super(hourOfDay, dayOfWeek);
        this.consuming = consuming;
    }
}
