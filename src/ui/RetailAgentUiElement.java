package ui;

import agents.models.Proposal;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.RetailerStatusContainer;
import ui.containers.StatusContainerBase;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Aswin Lakshman on 27/09/2016.
 */
public class RetailAgentUiElement extends AbstractAgentUiElement {
    private JLabel currentlyBuyingAt;
    private JLabel currentlySellingAt;
    private JLabel duration;

    public RetailAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, false, true);

        currentlyBuyingAt = new JLabel();
        currentlySellingAt = new JLabel();
        duration = new JLabel();

        this.add(currentlyBuyingAt, getGridBagConstraints());
        this.add(currentlySellingAt, getGridBagConstraints());
        this.add(duration, getGridBagConstraints());
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        RetailerStatusContainer status = (RetailerStatusContainer)currentStatus;

        // We haven't implemented a multi proposal system, so render the first proposal only..
        Proposal p = status.currentProposals.firstElement();

        currentlyBuyingAt.setText(String.format("Buying @ $%.2f", p.retailerBuyingPrice));
        currentlySellingAt.setText(String.format("Selling @ $%.2f", p.retailerSellingPrice));
        duration.setText(String.format("Duration %s hours", p.duration));
    }
}
