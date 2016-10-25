package agents.models;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.util.Random;

/**
 * Created by fegwin on 16/10/2016.
 */
public class DayUsageProfile {
    public DayOfWeek dayOfWeek;
    public double offPeakUsage; // kwH

    public SampleUsagePoint[] sampleUsagePoints; // hours between which peak runs (see validation)
    private double randMin;
    private double randMax;
    private Random random = new Random();

    public DayUsageProfile(DayOfWeek dayOfWeek, double offPeakUsage, SampleUsagePoint[] sampleUsagePoints, double randMin, double randMax) {
        this.dayOfWeek = dayOfWeek;
        this.offPeakUsage = offPeakUsage;

        this.sampleUsagePoints = sampleUsagePoints;
        this.randMin = randMin;
        this.randMax = randMax;

        if(!isValid()) throw new InvalidParameterException("Invalid DayUsageProfile params");
    }
  
    public DayUsageProfile(DayOfWeek dayOfWeek, double offPeakUsage, SampleUsagePoint[] sampleUsagePoints) {
    	this(dayOfWeek, offPeakUsage, sampleUsagePoints, 1.0, 1.0);
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
        		usage *= random.nextDouble()*(randMax-randMin) + randMin;
        	}
        }
        
        return usage;
    }
}
