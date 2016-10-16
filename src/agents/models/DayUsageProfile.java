package agents.models;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;

/**
 * Created by fegwin on 16/10/2016.
 */
public class DayUsageProfile {
    public DayOfWeek dayOfWeek;
    public int peakUsage; // kwH
    public int offPeakUsage; // kwH

    public PeakUsagePeriod[] peakUsagePeriods; // hours between which peak runs (see validation)

    public DayUsageProfile(DayOfWeek dayOfWeek, int peakUsage, int offPeakUsage, PeakUsagePeriod[] peakUsagePeriods) {
        this.dayOfWeek = dayOfWeek;
        this.peakUsage = peakUsage;
        this.offPeakUsage = offPeakUsage;

        this.peakUsagePeriods = peakUsagePeriods;

        if(!isValid()) throw new InvalidParameterException("Invalid DayUsageProfile params");
    }

    private boolean isValid() {
        if(peakUsagePeriods == null) return false;

        return true;
    }

    public int getCurrentConsumptionValue(int hourOfDay) {
        // are we in peak period?
        boolean isInPeak = false;

        for(PeakUsagePeriod pup : peakUsagePeriods) {
            if(pup.isInPeakPeriod(hourOfDay)) isInPeak = true;
        }

        return isInPeak ? peakUsage : offPeakUsage;
    }
}
