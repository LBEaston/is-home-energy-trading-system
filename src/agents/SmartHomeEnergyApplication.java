package agents;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.Exchanger;

/**
 * Created by fegwin on 7/09/2016.
 */
public class SmartHomeEnergyApplication implements Runnable, AgentStatusChangeEvent {
    private JPanel rootPanel;

    private Vector<AgentController> allAgents;
    private Map<String, JComponent> agentStatusContainers;

    public SmartHomeEnergyApplication(Vector<AgentController> allAgents) {
        this.allAgents = allAgents;
        agentStatusContainers = new HashMap();
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

        rootContainer.add(rootPanel);

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
        String agentId = agentController.getName();

        // Get agent o2a interface and register event
        Observable oa = agentController.getO2AInterface(Observable.class);

        if(oa == null) throw new UnableToGetAgentInterfaceException();

        oa.addStatusEventListener(this);

        // Create Status Container
        JComponent agentStatusContainer = new JLabel(getAgentSimpleName(agentId));
        agentStatusContainer.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        agentStatusContainers.put(agentId, agentStatusContainer);

        // Add click handler
        agentStatusContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    agentController.getO2AInterface(Destroyable.class).destroy();
                } catch (StaleProxyException e1) {
                    e1.printStackTrace();
                }
            }
        });

        return agentStatusContainer;
    }

    @Override
    public void inform(String agentIdentifier, String currentStatus) {
        // Get the component
        JLabel statusContainer = (JLabel) agentStatusContainers.get(agentIdentifier);

        statusContainer.setText("<html><b>" + getAgentSimpleName(agentIdentifier) + "</b><br/>" + currentStatus + "</html>");
    }

    private String getAgentSimpleName(String agentIdentifier) {
        return agentIdentifier.split("@")[0];
    }
}
