package ui;

import agents.models.Proposal;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.RetailerStatusContainer;
import ui.containers.StatusContainerBase;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class RetailAgentUiElement extends AbstractAgentUiElement {
	private static final long serialVersionUID = 1L;
    private JLabel currentProposal;

    public RetailAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, false, true);

        currentProposal = new JLabel();
        this.add(currentProposal, getGridBagConstraints());
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        RetailerStatusContainer status = (RetailerStatusContainer)currentStatus;

        String proposalString = "";

        for(Proposal p : status.currentProposals) {
            proposalString += String.format("<%s>", p.toReadableString());
        }

        currentProposal.setText(proposalString);
    }
}
