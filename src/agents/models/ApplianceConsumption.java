package agents.models;

import java.time.DayOfWeek;

public class ApplianceConsumption {
    public double consuming;
    public int hourOfDay;
    public DayOfWeek dayOfWeek;

    // What's up with consuming2 ??? -Lachlan
    // public ApplianceConsumption(double consuming2, int hourOfDay, DayOfWeek dayOfWeek) {
    //     this.consuming = consuming2;
    public ApplianceConsumption(double consuming, int hourOfDay, DayOfWeek dayOfWeek) {
        this.consuming = consuming;
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
    }
}
