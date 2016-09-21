package agents;

/**
 * Created by fegwin on 15/09/2016.
 */
public interface Observable {
    void addStatusEventListener(AgentStatusChangeEvent listener);
}
