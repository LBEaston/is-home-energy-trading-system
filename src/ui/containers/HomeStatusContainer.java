package ui.containers;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class HomeStatusContainer extends StatusContainerBase {
    public int currentNetConsumption;

    public HomeStatusContainer(int currentNetConsumption, int hourOfDay, DayOfWeek dayOfWeek) {
        super(hourOfDay, dayOfWeek);

        this.currentNetConsumption = currentNetConsumption;
    }
}
