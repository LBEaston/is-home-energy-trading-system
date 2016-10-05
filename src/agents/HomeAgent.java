package agents;

import jade.core.AID;

import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.core.behaviours.CyclicBehaviour;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.*
;/**
 * Created by fegwin on 7/09/2016.
 */
public class HomeAgent extends AbstractAgent {
    private Vector<String> retailers;
    private boolean inTheMiddleOfANegotiation = false;

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
        
        /* TODO(Lachlan 28-9-16) Fill with historical data??? */
        _kwhProducedInPast.add(0.f);
        _kwhUsedInPast.add(0.f);
        _kwhNet.add(0.f);
        addBehaviour(new CyclicBehaviour(this) 
        {
			private static final long serialVersionUID = 1L;

			public void action() 
            {
                ACLMessage msg = receive();
                if (msg!=null)
                	System.out.println("Recieved MSG: " + msg.getContent());
                
                float kwh = Integer.parseInt( msg.getContent().replace("consuming=", "") );
                
                block();
            }
        });
    }
    
    /* NOTE(Lachlan 28-9-16) History of power produced/used */
    private ArrayList<Float> _kwhProducedInPast = new ArrayList<Float>();
    private ArrayList<Float> _kwhUsedInPast = new ArrayList<Float>();
    private ArrayList<Float> _kwhNet = new ArrayList<Float>(); /* NOTE(Lachlan 28-9-16) Same as (Used - Produced) Possibly too redundant? */
    private float predictKWHForNextNHours(float n)
    {
    	/* TODO(Lachlan 28-9-16) Use linear regression/other predicition techniques */
    	float result = 0;
    	result = n * _kwhUsedInPast.get(_kwhUsedInPast.size()-1);
    	return result;
    }

    public void configureBehaviours() {
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

                // Get a handle not all proposals
                ArrayList<ACLMessage> proposals = new ArrayList<ACLMessage>();
                while (e.hasMoreElements()) {
                    ACLMessage msg = (ACLMessage) e.nextElement();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        proposals.add(msg);
                    }
                }

                // Extract Contract proposals from messages
                ArrayList<Contract> proposed_contracts = new ArrayList<Contract>();
                for(ACLMessage msg : proposals)
                {
                    Contract c = new Contract();
                    c.associatedMessage = msg;
                    // TODO(Lachlan 5th October 2016)
                }

                // Find proposal with lowest predicted cost/hour
                Contract bestProposal = null;
                for(Contract c : proposed_contracts)
                {
                    float predicted_kwh = predictKWHForNextNHours(c.durationInSeconds * 3600);
                    c.predictedSpendaturePerHour = predicted_kwh / (c.durationInSeconds * 3600);
                    if(bestProposal == null || c.predictedSpendaturePerHour < bestProposal.predictedSpendaturePerHour)
                    {
                        bestProposal = c;
                    }
                }

                // Accept best proposal
                {
                    ACLMessage msg = bestProposal.associatedMessage;
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    acceptances.addElement(reply);

                    proposed_contracts.remove(msg);
                }

                // Reject remaining elements
                for(ACLMessage msg : proposals)
                {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.addElement(reply);
                }
                inTheMiddleOfANegotiation = false;
            }

            protected void handleInform(ACLMessage inform) {
                fireStatusChangedEvent("Agent " + inform.getSender().getLocalName() + " is informing");
            }
        });
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.HomeAgent;
    }
    
    
    }

