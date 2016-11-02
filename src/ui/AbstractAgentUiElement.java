package ui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ui.containers.StatusContainerBase;
import ui.interfaces.Informable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Aswin Lakshman on 27/09/2016.
 */
public abstract class AbstractAgentUiElement extends JPanel implements Informable {
	protected AgentController agentController;
    protected boolean showDateAndTime;
    protected boolean showAgentName;

    private JLabel currentHourOfDay;
    private JLabel currentDayOfWeek;

    AbstractAgentUiElement(AgentController agentController, boolean showDateAndTime, boolean showAgentName) throws StaleProxyException {
        super();
        this.showDateAndTime = showDateAndTime;
        this.showAgentName = showAgentName;
        this.setLayout(new GridBagLayout());

        this.setBackground(Color.white);

        this.agentController = agentController;
        createLayout();
    }

    protected void createLayout() throws StaleProxyException {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        if(showAgentName) {
            // Add the title
            JLabel title = new JLabel(getAgentSimpleName());
            Font font = title.getFont();
            Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
            title.setFont(boldFont);

            this.add(title, getGridBagConstraints());
        }

        if(showDateAndTime) {
            // add timekeeping
            currentHourOfDay = new JLabel();
            currentDayOfWeek = new JLabel();

            this.add(currentDayOfWeek, getGridBagConstraints());
            this.add(currentHourOfDay, getGridBagConstraints());
        }
    }

    public static GridBagConstraints getGridBagConstraints() {
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1;
        cons.gridx = 0;

        return cons;
    }

    protected String getAgentSimpleName() throws StaleProxyException {
        return agentController.getName().split("@")[0];
    }

    @Override
    public void inform(StatusContainerBase currentStatus) {
        if(showDateAndTime) {
            String weeksText = "Day of week: " + currentStatus.dayOfWeek.toString();

            if(currentStatus.weeksElapsed > 0) {
                weeksText += " (" + currentStatus.weeksElapsed + " weeks elapsed)";
            }

            currentDayOfWeek.setText(weeksText);
            currentHourOfDay.setText("Hour of day: " + currentStatus.hourOfDay);
        }
    }
}
