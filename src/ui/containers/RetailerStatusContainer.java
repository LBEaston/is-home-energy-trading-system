package ui.containers;

import ui.StatusContainerBase;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class RetailerStatusContainer extends StatusContainerBase {
    public RetailerStatusContainer(int hourOfDay, DayOfWeek dayOfWeek) {
        super(hourOfDay, dayOfWeek);
    }
}
