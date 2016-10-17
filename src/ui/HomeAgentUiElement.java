package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.HomeStatusContainer;
import ui.containers.StatusContainerBase;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class HomeAgentUiElement extends AbstractAgentUiElement {
    private JLabel currentNetConsumption;
    private JLabel previousNetConsumption;

    private JLabel currentContractDetails;

    public HomeAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController);

        currentNetConsumption = new JLabel();
        previousNetConsumption = new JLabel();

        this.add(currentNetConsumption);
        this.add(previousNetConsumption);
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        HomeStatusContainer status = (HomeStatusContainer)currentStatus;

        String previous = currentNetConsumption.getText();
        currentNetConsumption.setText("Current net: " + status.currentNetConsumption + " kwH");
        previousNetConsumption.setText(previous.replace("Current", "Previous"));
    }
}
