package ui.containers;

import agents.models.Proposal;

import java.time.DayOfWeek;
import java.util.Vector;

/**
 * Created by fegwin on 13/10/2016.
 */
public class RetailerStatusContainer extends StatusContainerBase {
    public Vector<Proposal> currentProposals;

    public RetailerStatusContainer(int hourOfDay, DayOfWeek dayOfWeek, Vector<Proposal> proposals) {
        super(hourOfDay, dayOfWeek);

        currentProposals = proposals;
    }
}
