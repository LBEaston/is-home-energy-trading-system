package agents;

import agents.interfaces.ApplianceAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ui.interfaces.Informable;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 7/09/2016.
 */
public class SimpleApplianceAgent extends AbstractAgent implements ApplianceAgent {
    protected int consumptionValue = 0;
    protected String homeAgentName = null;

    @Override
    public int currentlyConsuming() {
        return consumptionValue;
    }

    @Override
    public boolean isActive() {
        return consumptionValue != 0;
    }

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length < 2) throw new InvalidParameterException("Have not provided starting consumption value");

        //Always on agent requires a consumption value
        String homeAgentName = (String)args[0];
        int consumptionValue = (int)args[1];

        this.consumptionValue = consumptionValue;
        this.homeAgentName = homeAgentName;
    }

    @Override
    protected void configureBehaviours() {
        addBehaviour(getSayHelloBehaviour());
        addBehaviour(getInformBehaviour());
    }

    protected OneShotBehaviour getSayHelloBehaviour() {
        return new OneShotBehaviour() {
            @Override
            public void action() {
                sayHelloToHomeAgent();
            }
        };
    }

    protected OneShotBehaviour getInformBehaviour() {
        return new OneShotBehaviour() {
            @Override
            public void action() {
                informCurrentlyConsuming();
            }
        };
    }

    protected void informCurrentlyConsuming() {
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);

        informMessage.setSender(new AID(this.getLocalName(), AID.ISLOCALNAME));
        informMessage.addReceiver(new AID(homeAgentName, AID.ISLOCALNAME));
        informMessage.setContent("consuming=" + currentlyConsuming());
        informMessage.setOntology("homeenergy");
        informMessage.setLanguage("english");

        send(informMessage);
        fireStatusChangedEvent(currentlyConsuming());
    }

    private void sayHelloToHomeAgent() {
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);

        informMessage.setSender(new AID(this.getLocalName(), AID.ISLOCALNAME));
        informMessage.addReceiver(new AID(homeAgentName, AID.ISLOCALNAME));
        informMessage.setContent("hello" + currentlyConsuming());
        informMessage.setOntology("homeenergy");
        informMessage.setLanguage("english");

        send(informMessage);
    }

    @Override
    public void addStatusEventListener(Informable listener) {
        super.addStatusEventListener(listener);

        // New listener added, do an inform
        addBehaviour(getInformBehaviour());
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.ApplianceAgent;
    }
}
