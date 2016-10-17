package agents.models;

import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;

/**
 * Created by fegwin on 16/10/2016.
 */
public class ApplianceProfile {
    public String applianceName;
    public DayUsageProfile[] dayUsageProfiles;

    public ApplianceProfile(String applianceName, DayUsageProfile[] dayUsageProfiles) {
        this.applianceName = applianceName;
        this.dayUsageProfiles = dayUsageProfiles;

        if(!isValid()) throw new InvalidParameterException("Invalid ApplianceProfile params");
    }

    public int getCurrentConsumptionValue(DayOfWeek dayOfWeek, int hourOfDay) throws EvaluationException {
        DayUsageProfile current = null;

        for(DayUsageProfile dup : dayUsageProfiles) {
            if(dup.dayOfWeek == dayOfWeek) {
                current = dup;
                break;
            }
        }

        if(current == null) throw new EvaluationException("Unable to evaluate day usage");

        return current.getCurrentConsumptionValue(hourOfDay);
    }

    private boolean isValid() {
        if(dayUsageProfiles == null) return false;
        if(dayUsageProfiles.length != 7) return false;

        boolean hasMonday = false;
        boolean hasTuesday = false;
        boolean hasWednesday = false;
        boolean hasThursday = false;
        boolean hasFriday = false;
        boolean hasSaturday = false;
        boolean hasSunday = false;

        for(DayUsageProfile d : dayUsageProfiles) {
            if (d.dayOfWeek == DayOfWeek.MONDAY) hasMonday = true;
            if (d.dayOfWeek == DayOfWeek.TUESDAY) hasTuesday = true;
            if (d.dayOfWeek == DayOfWeek.WEDNESDAY) hasWednesday = true;
            if (d.dayOfWeek == DayOfWeek.THURSDAY) hasThursday = true;
            if (d.dayOfWeek == DayOfWeek.FRIDAY) hasFriday = true;
            if (d.dayOfWeek == DayOfWeek.SATURDAY) hasSaturday = true;
            if (d.dayOfWeek == DayOfWeek.SUNDAY) hasSunday = true;
        }

        return hasMonday && hasTuesday && hasWednesday && hasThursday && hasFriday && hasSaturday && hasSunday;
    }
}