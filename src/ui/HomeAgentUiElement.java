package ui;

import agents.interfaces.Observable;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public class HomeAgentUiElement extends AbstractAgentUiElement {
    JLabel statusString;

    public HomeAgentUiElement(AgentController agentController) throws StaleProxyException {
        super(agentController);
    }

    @Override
    protected void createLayout() throws StaleProxyException {
        super.createLayout();

        statusString = new JLabel();
    }

    @Override
    public void inform(Object currentStatus) {

    }
}
