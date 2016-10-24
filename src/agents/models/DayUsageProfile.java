package agents.models;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;

/**
 * Created by fegwin on 16/10/2016.
 */
public class DayUsageProfile {
    public DayOfWeek dayOfWeek;
    public double offPeakUsage; // kwH

    public SampleUsagePoint[] sampleUsagePoints; // hours between which peak runs (see validation)

    public DayUsageProfile(DayOfWeek dayOfWeek, double offPeakUsage, SampleUsagePoint[] sampleUsagePoints) {
        this.dayOfWeek = dayOfWeek;
        this.offPeakUsage = offPeakUsage;

        this.sampleUsagePoints = sampleUsagePoints;

        if(!isValid()) throw new InvalidParameterException("Invalid DayUsageProfile params");
    }

    private boolean isValid() {
        if(sampleUsagePoints == null) return false;

        return true;
    }

    public double getCurrentConsumptionValue(int hourOfDay) {

        double usage = offPeakUsage;
        for(SampleUsagePoint pup : sampleUsagePoints) {
            if(pup.isInPeriod(hourOfDay)) 
        	{
        		usage = pup.usage;
        	}
        }

        return usage;
    }
}
