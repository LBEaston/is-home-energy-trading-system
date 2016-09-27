package ui;

import agents.interfaces.Observable;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.interfaces.Informable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;

/**
 * Created by fegwin on 27/09/2016.
 */
public abstract class AbstractAgentUiElement extends JPanel implements Informable {
    protected AgentController agentController;

    AbstractAgentUiElement(AgentController agentController) throws StaleProxyException {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.white);

        this.agentController = agentController;
        createLayout();
    }

    protected void createLayout() throws StaleProxyException {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add the title
        JLabel title = new JLabel(getAgentSimpleName(), JLabel.CENTER);
        this.add(title);
    }

    protected String getAgentSimpleName() throws StaleProxyException {
        return agentController.getName().split("@")[0];
    }
}
