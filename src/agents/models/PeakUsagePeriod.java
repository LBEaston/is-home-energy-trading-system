package agents.models;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 16/10/2016.
 */
public class PeakUsagePeriod {
    protected int startHour;
    protected int finishHour;
    protected int duration;

    public PeakUsagePeriod(int startHour, int finishHour, int duration) {
        this.startHour = startHour;
        this.finishHour = finishHour;
        this.duration = duration;

        if(!isValid()) throw new InvalidParameterException("Invalid PeakUsageParams");
    }

    public int maxPeakDuration() {
        return finishHour - startHour;
    }

    private boolean isValid() {
        if(startHour < 0 || startHour > 23) return false;
        if(finishHour < 0 || finishHour > 23) return false;
        if(startHour > finishHour) return false;
        if(duration > maxPeakDuration()) return false;

        return true;
    }

    public boolean isInPeakPeriod(int hourOfDay) {
        // Currently we are ignoring duration (but we should set that up on construction to get
        // randomised peak period within start/finish time provided for given duration - seems coolish)
        if(hourOfDay >= startHour && hourOfDay <= finishHour) return true;

        return false;
    }
}
