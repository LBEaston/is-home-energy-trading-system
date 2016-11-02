package ui;

import agents.interfaces.Observable;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by Aswin Lakshman on 7/09/2016.
 */
public class SmartHomeEnergyApplication implements Runnable {
    private Vector<AgentController> allAgents;

    public SmartHomeEnergyApplication(Vector<AgentController> allAgents) {
        this.allAgents = allAgents;
    }

    @Override
    public void run() {
        try {
            populateWithAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (UnableToGetAgentInterfaceException e) {
            e.printStackTrace();
        }
    }

    private void populateWithAgents() throws StaleProxyException, UnableToGetAgentInterfaceException {
        Vector<AgentController> retailers = new Vector();
        Vector<AgentController> homeAgents = new Vector();
        Vector<AgentController> applianceAgents = new Vector();

        // Sorting the agents first
        for(AgentController agentController : allAgents) {
            // Get agent o2a interface and register event
            agents.interfaces.Observable oa = agentController.getO2AInterface(Observable.class);

            if(oa == null) throw new UnableToGetAgentInterfaceException();

            switch(oa.getAgentType()) {
                case RetailerAgent:
                    retailers.add(agentController);
                    break;
                case HomeAgent:
                    homeAgents.add(agentController);
                    break;
                case ApplianceAgent:
                    applianceAgents.add(agentController);
                    break;
            }
        }

        // Create retailer UI
        createRetailerUi(retailers);

        // Create home agent UIs - including appliance agents that belong to them
        for(AgentController agentController : homeAgents) {
            createHomeAgentUi(agentController, applianceAgents);
        }
    }

    private void createHomeAgentUi(AgentController agentController, Vector<AgentController> applianceAgents) throws StaleProxyException, UnableToGetAgentInterfaceException {
        JPanel appliancePanel = new JPanel();
        appliancePanel.setLayout(new GridBagLayout());

        Observable homeAgent = agentController.getO2AInterface(Observable.class);

        if(homeAgent == null) throw new UnableToGetAgentInterfaceException();

        for(AgentController ac : applianceAgents) {
            // Get agent o2a interface for the
            Observable appliance = ac.getO2AInterface(Observable.class);

            if(appliance == null) throw new UnableToGetAgentInterfaceException();

            if(!appliance.getAgentGroup().equals(homeAgent.getAgentGroup())) continue;

            // Appliance belongs to home agent...
            AbstractAgentUiElement applianceUiElement = new ApplianceAgentUiElement(ac);
            appliancePanel.add(applianceUiElement, AbstractAgentUiElement.getGridBagConstraints());

            // Subscribe the ui to the agent events
            appliance.addStatusEventListener(applianceUiElement);
        }

        AbstractAgentUiElement homeAgentUiElement = new HomeAgentUiElement(agentController);

        homeAgent.addStatusEventListener(homeAgentUiElement);

        // Show the window
        JFrame homeAgentWindow = new JFrame(homeAgent.getAgentGroup());

        homeAgentWindow.setLayout(new GridBagLayout());

        GridBagConstraints homeAgentCons = AbstractAgentUiElement.getGridBagConstraints();
        homeAgentCons.fill = GridBagConstraints.BOTH;
        homeAgentCons.weighty = 1;

        homeAgentWindow.add(homeAgentUiElement, homeAgentCons);
        homeAgentWindow.add(appliancePanel, AbstractAgentUiElement.getGridBagConstraints());
        homeAgentWindow.pack();
        homeAgentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeAgentWindow.setVisible(true);
    }

    private void createRetailerUi(Vector<AgentController> retailers) throws StaleProxyException {
        JFrame retailerPanel = new JFrame("Energy Retailers");

        retailerPanel.setLayout(new GridBagLayout());

        for(AgentController ac : retailers) {
            Observable oa = ac.getO2AInterface(Observable.class);

            AbstractAgentUiElement agentUiElement = new RetailAgentUiElement(ac);
            oa.addStatusEventListener(agentUiElement);

            retailerPanel.add(agentUiElement, AbstractAgentUiElement.getGridBagConstraints());
        }

        retailerPanel.pack();
        retailerPanel.setVisible(true);
    }

}
