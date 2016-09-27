package agents;

import agents.interfaces.ApplianceAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 7/09/2016.
 */
public class AlwaysOnApplianceAgent extends AbstractAgent implements ApplianceAgent {
    private int consumptionValue = 0;
    private String homeAgentName = null;

    @Override
    public int currentlyConsuming() {
        return consumptionValue;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length != 2) throw new InvalidParameterException("Have not provided starting consumption value");

        //Always on agent requires a consumption value
        String homeAgentName = (String)args[0];
        int consumptionValue = (int)args[1];

        this.consumptionValue = consumptionValue;
        this.homeAgentName = homeAgentName;
    }

    @Override
    protected void configureBehaviours() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                informCurrentlyConsuming();
            }
        });
    }

    private void informCurrentlyConsuming() {
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);

        informMessage.setSender(new AID(homeAgentName, AID.ISLOCALNAME));
        informMessage.setContent("consuming=" + currentlyConsuming());
        informMessage.setOntology("homeenergy");

        send(informMessage);
        fireStatusChangedEvent(currentlyConsuming());
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.ApplianceAgent;
    }
}
