package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.HomeStatusContainer;

/**
 * Created by fegwin on 27/09/2016.
 */
public class HomeAgentUiElement extends AbstractAgentUiElement {
    public HomeAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController);
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        HomeStatusContainer status = (HomeStatusContainer)currentStatus;
    }
}
