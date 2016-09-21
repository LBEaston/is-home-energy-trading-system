package agents;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by fegwin on 7/09/2016.
 */
public class SmartHomeEnergyApplication implements Runnable, AgentStatusChangeEvent {
    private Vector<AgentController> agents;
    private Map<String, JComponent> agentStatusContainers;

    public SmartHomeEnergyApplication(Vector<AgentController> agents) {
        this.agents = agents;
        agentStatusContainers = new HashMap();
    }

    @Override
    public void run() {
        JFrame rootContainer = new JFrame("Smart Home Energy");

        rootContainer.setSize(new Dimension(100, 300));
        rootContainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            createLayout(rootContainer);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        rootContainer.pack();
        rootContainer.setVisible(true);
    }

    private void createLayout(JFrame rootContainer) throws StaleProxyException {
        // Root layout
        rootContainer.setLayout(new FlowLayout(FlowLayout.CENTER));

        Vector<JComponent> items = new Vector<>();

        for(AgentController agent : agents) {
            items.add(createAgentStatusContainerWithSubscription(agent));
        }

        items.forEach(rootContainer::add);
    }

    private JComponent createAgentStatusContainerWithSubscription(AgentController agentController) throws StaleProxyException {
        String agentId = agentController.getName();

        // Create Status Container
        JComponent agentStatusContainer = new JLabel(agentId);
        agentStatusContainer.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        // Get agent o2a interface and register event
        Observable oa = agentController.getO2AInterface(Observable.class);

        if(oa != null) oa.addStatusEventListener(this);

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

        statusContainer.setText(agentIdentifier + "|" + currentStatus);
    }
}
