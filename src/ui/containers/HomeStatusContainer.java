package ui.containers;

import agents.models.Proposal;

import java.time.DayOfWeek;

/**
 * Created by Aswin Lakshman on 13/10/2016.
 */
public class HomeStatusContainer extends StatusContainerBase {
    public double currentNetConsumption;
    public double totalSpendToDate;
    public Proposal currentEnergyContract;
    public double[] graphScoresPrediction;


    public HomeStatusContainer(double currentNetConsumption, double totalSpendToDate,
                               int hourOfDay, DayOfWeek dayOfWeek, int weeksElapsed,
                               Proposal contract, double[] graphScoresPrediction) {

        super(hourOfDay, dayOfWeek, weeksElapsed);

        this.totalSpendToDate = totalSpendToDate;
        this.currentNetConsumption = currentNetConsumption;
        this.currentEnergyContract = contract;
        this.graphScoresPrediction = graphScoresPrediction;
    }
}
