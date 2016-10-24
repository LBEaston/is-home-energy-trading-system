package ui;

import agents.interfaces.Observable;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by fegwin on 7/09/2016.
 */
public class SmartHomeEnergyApplication implements Runnable {
    private JComponent rootPanel;

    private Vector<AgentController> allAgents;

    public SmartHomeEnergyApplication(Vector<AgentController> allAgents) {
        this.allAgents = allAgents;
    }

    @Override
    public void run() {
        JFrame rootContainer = new JFrame("Smart Home Energy");

        rootContainer.setPreferredSize(new Dimension(400, 800));
        rootContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayout(0, 1));

        try {
            populateWithAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (UnableToGetAgentInterfaceException e) {
            e.printStackTrace();
        }
        
        rootContainer.add(rootPanel);
        rootContainer.pack();
        rootContainer.setVisible(true);
    }

    private void populateWithAgents() throws StaleProxyException, UnableToGetAgentInterfaceException {

        for(AgentController agent : allAgents) {
            createAgentStatusContainerWithSubscription(agent);
        }
    }

    private void createAgentStatusContainerWithSubscription(AgentController agentController) throws StaleProxyException, UnableToGetAgentInterfaceException {
        // Get agent o2a interface and register event
        agents.interfaces.Observable oa = agentController.getO2AInterface(Observable.class);

        if(oa == null) throw new UnableToGetAgentInterfaceException();

        AbstractAgentUiElement agentUiElement = null;

        // Create Agent Ui Element
        switch (oa.getAgentType())
        {
            case RetailerAgent:
                agentUiElement = new RetailAgentUiElement(agentController);
                rootPanel.add(agentUiElement);
                break;
            case HomeAgent:
                agentUiElement = new HomeAgentUiElement(agentController);
                rootPanel.add(agentUiElement);
                break;
            case ApplianceAgent:
                agentUiElement = new ApplianceAgentUiElement(agentController);
                rootPanel.add(agentUiElement);
                break;
        }

        oa.addStatusEventListener(agentUiElement);
    }
}
