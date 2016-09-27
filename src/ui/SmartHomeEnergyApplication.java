package ui;

import agents.interfaces.Observable;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by fegwin on 7/09/2016.
 */
public class SmartHomeEnergyApplication implements Runnable {
    private JPanel rootPanel;
    private JPanel retailerPanel;
    private JPanel appliancePanel;
    private JComponent homeAgnet;

    private Vector<AgentController> allAgents;

    public SmartHomeEnergyApplication(Vector<AgentController> allAgents) {
        this.allAgents = allAgents;
    }

    @Override
    public void run() {
        JFrame rootContainer = new JFrame("Smart Home Energy");

        rootContainer.setPreferredSize(new Dimension(500, 450));
        rootContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayout(3, 1));

        retailerPanel = new JPanel();
        retailerPanel.setLayout(new GridLayout(1, 3));

        appliancePanel = new JPanel();
        appliancePanel.setLayout(new GridLayout(3, 3));

        try {
            populateWithAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (UnableToGetAgentInterfaceException e) {
            e.printStackTrace();
        }

        rootPanel.add(retailerPanel);
        rootPanel.add(homeAgnet);
        rootPanel.add(appliancePanel);

        rootContainer.getContentPane().add(rootPanel);

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
                retailerPanel.add(agentUiElement);
                break;
            case HomeAgent:
                agentUiElement = new HomeAgentUiElement(agentController);
                homeAgnet = agentUiElement;
                break;
            case ApplianceAgent:
                agentUiElement = new ApplianceAgentUiElement(agentController);
                appliancePanel.add(agentUiElement);
                break;
        }

        oa.addStatusEventListener(agentUiElement);
    }
}
