package agents.models;

import java.time.DayOfWeek;

public class ApplianceConsumption {
    public float consuming;
    public int hourOfDay;
    public DayOfWeek dayOfWeek;

    public ApplianceConsumption(float consuming, int hourOfDay, DayOfWeek dayOfWeek) {
        this.consuming = consuming;
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
    }
}
