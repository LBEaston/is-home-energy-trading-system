package ui.containers;

import agents.models.Proposal;

import java.time.DayOfWeek;

/**
 * Created by fegwin on 13/10/2016.
 */
public class HomeStatusContainer extends StatusContainerBase {
    public double currentNetConsumption;
    public double totalSpendToDate;
    public Proposal currentEnergyContract;

    public HomeStatusContainer(double currentNetConsumption, double totalSpendToDate, int hourOfDay, DayOfWeek dayOfWeek, Proposal contract) {
        super(hourOfDay, dayOfWeek);

        this.totalSpendToDate = totalSpendToDate;
        this.currentNetConsumption = currentNetConsumption;
        this.currentEnergyContract = contract;
    }
}
