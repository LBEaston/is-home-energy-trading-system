package agents;

import jade.core.Agent;

import java.util.Vector;

/**
 * Created by fegwin on 15/09/2016.
 */
public abstract class AbstractAgent extends Agent implements Observable, Destroyable {
    private Vector<AgentStatusChangeEvent> statusEventListeners;

    public void addStatusEventListener(AgentStatusChangeEvent listener) {
        if(statusEventListeners == null) {
            statusEventListeners = new Vector();
        }

        statusEventListeners.add(listener);
    }

    public void fireStatusChangedEvent(String newStatus) {
        if(statusEventListeners == null || statusEventListeners.isEmpty()) return;

        for(AgentStatusChangeEvent listener : statusEventListeners) {
            listener.inform(this.getName(), newStatus);
        }
    }

    public AbstractAgent() {
        registerO2AInterface(Observable.class, this);
        registerO2AInterface(Destroyable.class, this);
    }

    @Override
    public void destroy() {
        System.out.println(this.getLocalName() + " (" + this.getClass().getName() + ") - STOPPING");
        doDelete();
    }

    @Override
    protected void setup() {
        super.setup();
        System.out.println(this.getLocalName() + " (" + this.getClass().getName() + ") - STARTING");
    }
}
