package agents.interfaces;

import agents.EnergyAgentType;
import ui.interfaces.Informable;

/**
 * Created by Aswin Lakshman on 15/09/2016.
 */
public interface Observable {
    void addStatusEventListener(Informable listener);
    EnergyAgentType getAgentType();
    String getAgentGroup();
}
