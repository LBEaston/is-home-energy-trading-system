package agents;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by fegwin on 7/09/2016.
 */
public class HomeAgent extends AbstractAgent {
    private Vector<String> retailers;
    private boolean inTheMiddleOfANegotiation = false;

    private int currentConsumptionRequirement = 0;

    public HomeAgent() {
        retailers = new Vector();
    }

    protected void setup() {
        super.setup();

        // Grab the known retailers, from args
        Object[] args = getArguments();

        for(Object arg : args) {
            String retailer = (String)arg;
            retailers.add(retailer);
        }

        // Configure behaviours
        try
        {
            configureBehaviours();
        }
        catch(Exception e) {
            int i = 0;
        }
    }

    private void configureBehaviours() throws InterruptedException {
        // Add a cyclical behaviour to do the comms routine, which keeps adding
        // the contract net init behaviour
        TickerBehaviour tickerBehaviour = new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                negotiateWithRetailers();
            }
        };

        addBehaviour(tickerBehaviour);
    }

    private void negotiateWithRetailers() {
        if(inTheMiddleOfANegotiation) return;
        inTheMiddleOfANegotiation = true;

        ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);

        // Add cfp recipients
        for(String retailer : retailers) {
            cfpMessage.addReceiver(new AID(retailer, AID.ISLOCALNAME));
        }

        cfpMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        cfpMessage.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        cfpMessage.setContent("dummy-action");

        addBehaviour(new ContractNetInitiator(this, cfpMessage) {
            protected void handlePropose(ACLMessage propose, Vector v) {
                fireStatusChangedEvent("Agent " + propose.getSender().getLocalName() + " is proposing");
            }

            protected void handleRefuse(ACLMessage refuse) {
                fireStatusChangedEvent("Agent " + refuse.getSender().getLocalName() + " is refusing");
            }

            protected void handleFailure(ACLMessage failure) {
                fireStatusChangedEvent("Agent " + failure.getSender().getLocalName() + " is proposing");
            }

            protected void handleAllResponses(Vector responses, Vector acceptances) {
                fireStatusChangedEvent("Various responses received");

                // Evaluate proposals.
                ACLMessage accept = null;
                Enumeration e = responses.elements();

                while (e.hasMoreElements()) {
                    ACLMessage msg = (ACLMessage) e.nextElement();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.addElement(reply);
                    }
                }
                inTheMiddleOfANegotiation = false;
            }

            protected void handleInform(ACLMessage inform) {
                fireStatusChangedEvent("Agent " + inform.getSender().getLocalName() + " is informing");
            }
        });
    }
}
