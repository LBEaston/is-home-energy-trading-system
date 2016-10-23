package agents;

import agents.models.ApplianceConsumption;
import agents.models.ApplianceConsumptionHistory;
import agents.models.Proposal;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.core.behaviours.CyclicBehaviour;
import ui.containers.HomeStatusContainer;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.*

;/**
 * Created by fegwin on 7/09/2016.
 */
public class HomeAgent extends AbstractAgent {
	private static final long serialVersionUID = 1L;
    // Internal State Variables
    private Vector<String> retailers;
    private HashMap<String, ApplianceConsumptionHistory> applianceConsumptionHistory;

    private HashMap<String, ApplianceConsumption> currentApplianceConsumption;

    private Proposal currentEnergyContract = null;
    private int ticksTillNextNegotiation = 0;

    private boolean inTheMiddleOfANegotiation = false;

    public HomeAgent() {
        retailers = new Vector<String>();
        applianceConsumptionHistory = new HashMap<String, ApplianceConsumptionHistory>();
        currentApplianceConsumption = new HashMap<String, ApplianceConsumption>();
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.HomeAgent;
    }

    protected void setup() {
        super.setup();

        // Grab the known retailers, from args
        Object[] args = getArguments();

        for(Object arg : args) {
            String retailer = (String)arg;
            retailers.add(retailer);
        }
    }

    /** Behaviours and Control Logic **/
    @Override
    protected void configureBehaviours() {
        addBehaviour(getReceiveHelloMessagesBehaviour());
    }

    @Override
    protected void appTickElapsed() {
        // Negotiate/Predict all that jazz
        if(ticksTillNextNegotiation <= 0) {
            negotiateWithRetailers();
        }

        ticksTillNextNegotiation--;
        addBehaviour(getRecalculateAndUpdateBehaviour());
    }

    private void negotiateWithRetailers() {
        if(inTheMiddleOfANegotiation) return;

        addBehaviour(getRetailerNegotiationBehaviour());
    }

    private Behaviour getRetailerNegotiationBehaviour() {
        ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);

        // Add cfp recipients
        for(String retailer : retailers) {
            cfpMessage.addReceiver(new AID(retailer, AID.ISLOCALNAME));
        }

        cfpMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        cfpMessage.setReplyByDate(new Date(System.currentTimeMillis() + APP_TICK));

        inTheMiddleOfANegotiation = true;

        return new ContractNetInitiator(this, cfpMessage) {
        	private static final long serialVersionUID = 1L;
            protected void handlePropose(ACLMessage propose, Vector v) { }
            protected void handleRefuse(ACLMessage refuse) { }
            protected void handleFailure(ACLMessage failure) { }
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                // Evaluate proposals.
                Enumeration e = responses.elements();

                // Get a handle not all proposals
                Vector<ACLMessage> proposalMessages = new Vector<ACLMessage>();
                while (e.hasMoreElements()) {
                    ACLMessage msg = (ACLMessage) e.nextElement();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        proposalMessages.add(msg);
                    }
                }

                // Extract Contract proposals from messages
                Proposal bestProposal = null;
                double bestUtility = 0;
                ACLMessage bestMessage = null;
                Vector<Proposal> proposedContracts = new Vector<Proposal>();
                for(ACLMessage msg : proposalMessages)
                {
                    String compoundProposalString = msg.getContent();
                    Vector<Proposal> proposalsFromAgent = Proposal.fromCompoundString(compoundProposalString);

                    for(Proposal p : proposalsFromAgent)
                    {
                    	double currentUtility = getUtilityOfContract(p);
                    	bestProposal = p;
                        bestUtility = currentUtility;
                        proposedContracts.add(p);
                        bestMessage = msg;
                    }
                }

                // Accept best proposal
                ACLMessage reply = bestMessage.createReply();

                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(bestProposal.toString());

                acceptances.addElement(reply);

                proposedContracts.remove(bestProposal);

                // Reject remaining elements
                for(ACLMessage msg : proposalMessages)
                {
                	if (msg == bestMessage) continue;
                    ACLMessage rejectReply = msg.createReply();
                    rejectReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.addElement(rejectReply);
                }
                inTheMiddleOfANegotiation = false;
            }
            protected void handleInform(ACLMessage inform) {
                currentEnergyContract = Proposal.fromString(inform.getContent());
                ticksTillNextNegotiation = currentEnergyContract.duration;
            }
        };
    }
    
    private double getUtilityOfContract(Proposal c) {
    	double result = 0;
    	double predicted_kwh = predictKWHForNextNHours(c.duration);
    	
    	double buypriceContribution = -1.0;
		double sellpriceContribution = 0.1;
		
    	if (predicted_kwh > 0.0)
    	{
    		// predicted going to consume more
    		buypriceContribution = -1.0;
    		sellpriceContribution = 0.1;
    	}
    	else
    	{
    		// predicted going to produced more
    		buypriceContribution = -0.1;
    		sellpriceContribution = 1.0;
    	}
    	
    	result = (buypriceContribution*c.buyingPrice + sellpriceContribution*c.sellingPrice)/Math.sqrt((double)c.duration);
    	
    	return result;
    }

    private Behaviour getReceiveHelloMessagesBehaviour() {
        return new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			public void action() {
                MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchContent("hello"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM) );

                ACLMessage msg = receive(template);

                if(msg != null) {
                    registerAppliance(msg.getSender());
                }

                block();
            }
        };
    }

    private Behaviour getReceiveMessagesBehaviour(AID sender) {
        return new CyclicBehaviour(this) {
        	private static final long serialVersionUID = 1L;
            public void action() {
                MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchSender(sender),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM) );

                ACLMessage msg = receive(template);
                if (msg!=null) {
                    if(msg.getContent().contains("consuming")) {
                        handleApplianceAgentConsumptionInform(msg.getSender(), msg.getContent());
                    }
                }

                block();
            }
        };
    }

    private Behaviour getRecalculateAndUpdateBehaviour() {
        return new OneShotBehaviour() {
        	private static final long serialVersionUID = 1L;
            @Override
            public void action() {
                try {
                    Thread.sleep(250);
                    fireStatusChangedEvent(new HomeStatusContainer(getCurrentNetConsumption(), hourOfDay, dayOfWeek));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /** Prediction Logic **/
    private float predictKWHForNextNHours(float n) {
    	/* TODO(Lachlan 28-9-16) Use linear regression/other predicition techniques */
    	float result = 0;

    	return result;
    }

    public int getCurrentNetConsumption() {
        Vector<ApplianceConsumption> consumers = new Vector<ApplianceConsumption>();

        Iterator<Map.Entry<String, ApplianceConsumption>> it = currentApplianceConsumption.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, ApplianceConsumption> pair = (Map.Entry<String, ApplianceConsumption>) it.next();

            ApplianceConsumption applianceConsumption = (ApplianceConsumption) pair.getValue();
            consumers.add(applianceConsumption);
        }

        return calculateApplianceConsumption(consumers);
    }

    private int calculateApplianceConsumption(Vector<ApplianceConsumption> consumers) {
        int netConsumtion = 0;

        for(ApplianceConsumption ac : consumers) {
            netConsumtion += ac.consuming;
        }

        return netConsumtion;
    }

    /** Appliance Interaction Handlers **/
    private void handleApplianceAgentConsumptionInform(AID aid, String msg) {
        float kwh = Integer.parseInt( msg.replace("consuming=", "") );

        // Update the current consumption knowledge of this appliance
        ApplianceConsumption thisAppliance = currentApplianceConsumption.get(aid.getLocalName());

        // Agent has not yet registered
        if(thisAppliance == null) {
            return; // This should not happen
        }

        thisAppliance.consuming = kwh;
    }

    private void registerAppliance(AID aid) {
        // Create currently consuming AND history
        currentApplianceConsumption.put(aid.getLocalName(), new ApplianceConsumption(0, hourOfDay, dayOfWeek));
        applianceConsumptionHistory.put(aid.getLocalName(), new ApplianceConsumptionHistory());

        addBehaviour(getReceiveMessagesBehaviour(aid));
    }

    private void updateApplianceConsumptionHistory() {
        Iterator<Map.Entry<String, ApplianceConsumption>> it = currentApplianceConsumption.entrySet().iterator();
        while(it.hasNext()) {
        	Map.Entry<String, ApplianceConsumption> pair = (Map.Entry<String, ApplianceConsumption>) it.next();

            String agentIdentifier = (String) pair.getKey();
            ApplianceConsumption applianceConsumption = (ApplianceConsumption) pair.getValue();

            ApplianceConsumptionHistory ach = applianceConsumptionHistory.get(agentIdentifier);
            ach.history.add(new ApplianceConsumption(applianceConsumption.consuming, hourOfDay, dayOfWeek));
        }
    }
}

