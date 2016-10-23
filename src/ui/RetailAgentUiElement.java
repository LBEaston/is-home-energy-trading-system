package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.RetailerStatusContainer;
import ui.containers.StatusContainerBase;

/**
 * Created by fegwin on 27/09/2016.
 */
public class RetailAgentUiElement extends AbstractAgentUiElement {
	private static final long serialVersionUID = 1L;
    public RetailAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController, false);
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        super.inform(currentStatus);
        RetailerStatusContainer status = (RetailerStatusContainer)currentStatus;
    }
}
