package agents.models;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 16/10/2016.
 */
public class SampleUsagePoint {
    protected int startHour;
    protected int finishHour;
    public double usage;

    public SampleUsagePoint(int startHour, int finishHour, double usage) {
        this.startHour = startHour;
        this.finishHour = finishHour;
        this.usage = usage;

        if(!isValid()) throw new InvalidParameterException("Invalid SampleUsagePointParams");
    }

    public int maxPeakDuration() {
        return finishHour - startHour;
    }

    private boolean isValid() {
        if(startHour < 0 || startHour > 23) return false;
        if(finishHour < 0 || finishHour > 23) return false;
        if(startHour > finishHour) return false;
        
        return true;
    }

    public boolean isInPeriod(int hourOfDay) {
        // Currently we are ignoring duration (but we should set that up on construction to get
        // randomised peak period within start/finish time provided for given duration - seems coolish)
        if(hourOfDay >= startHour && hourOfDay <= finishHour) return true;

        return false;
    }
}
