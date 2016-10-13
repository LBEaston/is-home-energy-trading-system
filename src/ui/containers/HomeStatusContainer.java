package ui.containers;

import ui.StatusContainerBase;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class HomeStatusContainer extends StatusContainerBase {
    public HomeStatusContainer(int hourOfDay, DayOfWeek dayOfWeek) {
        super(hourOfDay, dayOfWeek);
    }
}
