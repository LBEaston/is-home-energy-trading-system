package agents;

/**
 * Created by fegwin on 15/09/2016.
 */
public interface AgentStatusChangeEvent {
    void inform(String agentIdentifier, String currentStatus);
}
