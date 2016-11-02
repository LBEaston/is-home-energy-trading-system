package agents.models;

import java.util.Vector;

/**
 * Created by Aswin Lakshman on 11/10/2016.
 */
public class ApplianceConsumptionHistory {
    public Vector<ApplianceConsumption> history;

    public ApplianceConsumptionHistory() {
        history = new Vector<ApplianceConsumption>();
    }
}
