package ui.containers;

import ui.StatusContainerBase;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class ApplianceStatusContainer extends StatusContainerBase {
    public int consuming;

    public ApplianceStatusContainer(int consuming, int hourOfDay, DayOfWeek dayOfWeek) {
        super(hourOfDay, dayOfWeek);
        this.consuming = consuming;
    }
}
