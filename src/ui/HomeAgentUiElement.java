package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.HomeStatusContainer;
import ui.containers.StatusContainerBase;

import java.awt.Dimension;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class HomeAgentUiElement extends AbstractAgentUiElement {
	private static final long serialVersionUID = 1L;
    private JLabel currentNetConsumption;
    private JLabel previousNetConsumption;
    private JLabel currentContractDetails;
    
    private GraphPanel graph;
    private double[] graphScores = new double[24*7];
    
    public HomeAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, true);

        currentNetConsumption = new JLabel();
        previousNetConsumption = new JLabel();
        currentContractDetails = new JLabel();


        this.add(currentNetConsumption);
        this.add(previousNetConsumption);
        this.add(currentContractDetails);

        graph = new GraphPanel(graphScores);
        graph.setPreferredSize(new Dimension(1000, 200));
        
        JFrame homeUsageFrame = new JFrame("HomeUsage Graph");
        homeUsageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeUsageFrame.getContentPane().add(graph);
        homeUsageFrame.pack();
        homeUsageFrame.setLocationRelativeTo(null);
        homeUsageFrame.setVisible(true);
    }
    
    private int timeToGraph(HomeStatusContainer status)
    {
    	return (status.dayOfWeek.getValue()-1) *24 + status.hourOfDay;
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        HomeStatusContainer status = (HomeStatusContainer)currentStatus;

        String previous = currentNetConsumption.getText();
        currentNetConsumption.setText("Current net: " + status.currentNetConsumption + " kwH");
        previousNetConsumption.setText(previous.replace("Current", "Previous"));

        int graphIndex = timeToGraph(status);
        if(status.currentEnergyContract != null) {
            currentContractDetails.setText(String.format("Contract with %s.  Buying@%s.  Selling@%s.  Duration: %s",
                    status.currentEnergyContract.retailer,
                    status.currentEnergyContract.retailerSellingPrice,
                    status.currentEnergyContract.retailerBuyingPrice,
                    status.currentEnergyContract.duration));
            currentContractDetails.validate();

            graph.setStartEndTimes(graphIndex, 
            		graphIndex + status.currentEnergyContract.duration);
        }

        graphScores[(status.dayOfWeek.getValue()-1) *24 + status.hourOfDay] = ((double)status.currentNetConsumption);
        graph.setScores(graphScores);
        graph.setCurrentTime(graphIndex);
    }
}
