package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.RetailerStatusContainer;

/**
 * Created by fegwin on 27/09/2016.
 */
public class RetailAgentUiElement extends AbstractAgentUiElement {
    public RetailAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController);
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        RetailerStatusContainer status = (RetailerStatusContainer)currentStatus;
    }
}
