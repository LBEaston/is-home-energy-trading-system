package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.HomeStatusContainer;
import ui.containers.StatusContainerBase;

import java.awt.*;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class HomeAgentUiElement extends AbstractAgentUiElement {
	private static final long serialVersionUID = 1L;
    private JLabel currentNetConsumption;
    private JLabel totalSpendToDate;
    private JLabel currentContractDetails;

    private GraphPanel graph;
    private double[] graphScores = new double[24*7];
    
    public HomeAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, true, false);

        currentNetConsumption = new JLabel();
        totalSpendToDate = new JLabel();
        currentContractDetails = new JLabel();

        this.add(currentNetConsumption, getGridBagConstraints());
        this.add(totalSpendToDate, getGridBagConstraints());
        this.add(currentContractDetails, getGridBagConstraints());

        graph = new GraphPanel(graphScores);
        this.add(graph, getGraphGridBagConstraints());

        this.setPreferredSize(new Dimension(1000, 750));
    }
    
    private int timeToGraph(HomeStatusContainer status)
    {
    	return (status.dayOfWeek.getValue()-1) *24 + status.hourOfDay;
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        HomeStatusContainer status = (HomeStatusContainer)currentStatus;

        currentNetConsumption.setText("Current net: " + status.currentNetConsumption + " kwH");
        totalSpendToDate.setText("Total spend: " + status.totalSpendToDate);

        int graphIndex = timeToGraph(status);
        if(status.currentEnergyContract != null) {
            currentContractDetails.setText(String.format("Contract with %s.  Buying@%s.  Selling@%s.  Duration: %s",
                    status.currentEnergyContract.retailer,
                    status.currentEnergyContract.retailerSellingPrice,
                    status.currentEnergyContract.retailerBuyingPrice,
                    status.currentEnergyContract.duration));
            currentContractDetails.validate();

            graph.setStartEndTimes(graphIndex, graphIndex + status.currentEnergyContract.duration);
        }

        graphScores[(status.dayOfWeek.getValue()-1) *24 + status.hourOfDay] = ((double)status.currentNetConsumption);
        graph.setScores(graphScores);
        graph.setCurrentTime(graphIndex);
    }

    private GridBagConstraints getGraphGridBagConstraints() {
        GridBagConstraints cons = new GridBagConstraints();

        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.gridx = 0;

        return cons;
    }
}
