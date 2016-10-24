package agents.models;

import java.time.DayOfWeek;

public class ApplianceConsumption {
    public double consuming;
    public int hourOfDay;
    public DayOfWeek dayOfWeek;

    public ApplianceConsumption(double consuming2, int hourOfDay, DayOfWeek dayOfWeek) {
        this.consuming = consuming2;
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
    }
}
