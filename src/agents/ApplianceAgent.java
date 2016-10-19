package agents;

import agents.models.ApplianceProfile;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ui.containers.ApplianceStatusContainer;
import ui.interfaces.Informable;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 7/09/2016.
 */
public class ApplianceAgent extends AbstractAgent {
    protected ApplianceProfile applianceProfile = null;
    protected String homeAgentName = null;

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.ApplianceAgent;
    }

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length < 2) throw new InvalidParameterException("Have not provided starting consumption value");

        String homeAgentName = (String)args[0];
        ApplianceProfile ap = (ApplianceProfile)args[1];

        this.applianceProfile = ap;
        this.homeAgentName = homeAgentName;
    }

    /** Behaviours and Control Logic **/
    @Override
    protected void configureBehaviours() {
        addBehaviour(getSayHelloBehaviour());
    }

    @Override
    protected void appTickElapsed() {
        addBehaviour(getInformBehaviour());
    }

    protected OneShotBehaviour getInformBehaviour() {
        return new OneShotBehaviour() {
            @Override
            public void action() {
                informCurrentlyConsuming();
            }
        };
    }

    protected OneShotBehaviour getSayHelloBehaviour() {
        return new OneShotBehaviour() {
            @Override
            public void action() {
                sayHello();
            }
        };
    }

    protected void informCurrentlyConsuming() {
        int currentlyConsuming = currentlyConsuming();

        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);

        informMessage.setSender(new AID(this.getLocalName(), AID.ISLOCALNAME));
        informMessage.addReceiver(new AID(homeAgentName, AID.ISLOCALNAME));
        informMessage.setContent("consuming=" + currentlyConsuming);

        send(informMessage);
        fireStatusChangedEvent(new ApplianceStatusContainer(currentlyConsuming, hourOfDay, dayOfWeek));
    }

    protected void sayHello() {
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);

        informMessage.setSender(new AID(this.getLocalName(), AID.ISLOCALNAME));
        informMessage.addReceiver(new AID(homeAgentName, AID.ISLOCALNAME));
        informMessage.setContent("hello");

        send(informMessage);
    }

    private int currentlyConsuming() {
        try {
            return applianceProfile.getCurrentConsumptionValue(dayOfWeek, hourOfDay);
        }
            catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void addStatusEventListener(Informable listener) {
        super.addStatusEventListener(listener);

        // New listener added, do an inform
        addBehaviour(getInformBehaviour());
    }
}
