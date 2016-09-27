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

    private Vector<AgentController> allAgents;

    public SmartHomeEnergyApplication(Vector<AgentController> allAgents) {
        this.allAgents = allAgents;
    }

    @Override
    public void run() {
        JFrame rootContainer = new JFrame("Smart Home Energy");

        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        rootContainer.setPreferredSize(new Dimension(500, 300));
        rootContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            createLayout(rootContainer);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (UnableToGetAgentInterfaceException e) {
            e.printStackTrace();
        }

        rootContainer.getContentPane().add(rootPanel);

        rootContainer.pack();
        rootContainer.setVisible(true);
    }

    private void createLayout(JFrame rootContainer) throws StaleProxyException, UnableToGetAgentInterfaceException {
        Vector<JComponent> allComponents = new Vector<>();

        for(AgentController agent : allAgents) {
            allComponents.add(createAgentStatusContainerWithSubscription(agent));
        }

        allComponents.forEach(rootPanel::add);
    }

    private JComponent createAgentStatusContainerWithSubscription(AgentController agentController) throws StaleProxyException, UnableToGetAgentInterfaceException {
        // Get agent o2a interface and register event
        agents.interfaces.Observable oa = agentController.getO2AInterface(Observable.class);

        if(oa == null) throw new UnableToGetAgentInterfaceException();

        AbstractAgentUiElement agentUiElement = null;

        // Create Agent Ui Element
        switch (oa.getAgentType())
        {
            case RetailerAgent:
                agentUiElement = new RetailAgentUiElement(agentController);
                break;
            case HomeAgent:
                agentUiElement = new HomeAgentUiElement(agentController);
                break;
            case ApplianceAgent:
                agentUiElement = new ApplianceAgentUiElement(agentController);
                break;
        }

        oa.addStatusEventListener(agentUiElement);

        return agentUiElement;
    }
}
