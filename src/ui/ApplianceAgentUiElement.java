package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.ApplianceStatusContainer;
import ui.containers.StatusContainerBase;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class ApplianceAgentUiElement extends AbstractAgentUiElement {
	private static final long serialVersionUID = 1L;
    private JLabel currentlyConsuming;

    public ApplianceAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, false, true);
    }

    @Override
    protected void createLayout() throws StaleProxyException {
        super.createLayout();

        // Add the consuming
        currentlyConsuming = new JLabel();

        this.add(currentlyConsuming, getGridBagConstraints());
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        double currentConsumptionLevel = ((ApplianceStatusContainer)currentStatus).consuming;

        if(currentConsumptionLevel > 0) {
            this.setBackground(Color.red);
        } else if (currentConsumptionLevel < 0) {
            this.setBackground(Color.green);
        } else {
            this.setBackground(Color.white);
        }

        currentlyConsuming.setText(currentConsumptionLevel + "kwH");
    }
}
