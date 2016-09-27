package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class RetailAgentUiElement extends AbstractAgentUiElement {
    public RetailAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController);
    }

    @Override
    public void inform(Object currentStatus) {

    }
}
