package agents;

import jade.core.AID;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.core.behaviours.CyclicBehaviour;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*
;/**
 * Created by fegwin on 7/09/2016.
 */
public class HomeAgent extends AbstractAgent {
    private Vector<String> retailers;
    private HashMap<String, ApplianceConsumptionHistory> applianceConsumptionHistory;
    private HashMap<String, ApplianceConsumption> currentApplianceConsumption;

    /* NOTE(Lachlan 28-9-16) History of power produced/used **Lachie, this stuff can be calculated from the above arrays. I'll do it tomorrow**  */
    private ArrayList<Float> _kwhProducedInPast = new ArrayList<Float>();
    private ArrayList<Float> _kwhUsedInPast = new ArrayList<Float>();
    private ArrayList<Float> _kwhNet = new ArrayList<Float>(); /* NOTE(Lachlan 28-9-16) Same as (Used - Produced) Possibly too redundant? */
    private boolean inTheMiddleOfANegotiation = false;

    public HomeAgent() {
        retailers = new Vector();
        applianceConsumptionHistory = new HashMap();
        currentApplianceConsumption = new HashMap();

        /* TODO(Lachlan 28-9-16) Fill with historical data??? */
        _kwhProducedInPast.add(0.f);
        _kwhUsedInPast.add(0.f);
        _kwhNet.add(0.f);
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
                /*if(msg.getContent().contains("consumming"))
				{
					float kwh = Integer.parseInt( msg.getContent().replace("consuming=", "") );
				}
				else
				{
					// ...
				}*/
                
                
                block();
            }
        });
    }
   
    private float predictKWHForNextNHours(float n)
    {
    	/* TODO(Lachlan 28-9-16) Use linear regression/other predicition techniques */
    	float result = 0;
    	result = n * _kwhUsedInPast.get(_kwhUsedInPast.size()-1);
    	return result;
    }
    
    @Override
    protected void appTickElapsed() {
        // Update Appliance Consumption history for the tick just elapsed
        updateApplianceConsumptionHistory();

        // Negotiate/Predict all that jazz
        negotiateWithRetailers();
    }

    public void configureBehaviours() {
    	 //addBehaviour(getReceiveMessagesBehaviour());
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
					// * "{sellingAt=<FLOAT>;buyingAt=<FLOAT>;duration=<INT>}|{...}|{...}"
                	
                	String fullMsg = msg.getContent();
                	String[] proposalStrings = fullMsg.split("\\|");
                	
                	for(String pString : proposalStrings)
                	{
                		pString.replace("{", "");
                		pString.replace("}", "");
                		String[] components = pString.split(";");
                		components[0].replace("sellingAt=", "");
                		components[1].replace("buyingAt=", "");
                		components[2].replace("duration=", "");
                		Contract c = new Contract();
                        c.associatedMessage = msg;
    					//c.dolarsPerKWH        = Float.parseFloat( m.group(1) );
    					//c.dolarsPerKWHBuying  = Float.parseFloat( m.group(2) );
    					//c.durationInSeconds   = Float.parseFloat( m.group(3) );
    					proposed_contracts.add(c);
                	}
                }

                // Find proposal with lowest predicted cost/hour
                Contract bestProposal = null;
                for(Contract c : proposed_contracts)
                {
                    float predicted_kwh = predictKWHForNextNHours(c.durationInSeconds);
                    c.predictedSpendaturePerHour = predicted_kwh / (c.durationInSeconds);
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
    
    private Behaviour getReceiveMessagesBehaviour() {
        return new CyclicBehaviour(this)
        {
            public void action()
            {
                ACLMessage msg = receive();
                if (msg!=null) {
                    System.out.println("Recieved MSG: " + msg.getContent());

                    if(msg.getContent().contains("consuming")) {
                        handleApplianceAgentConsumptionInform(msg.getSender(), msg.getContent());
                    }
                }

                block();
            }
        };
    }

    /* Appliance Interaction Handlers */
    private void handleApplianceAgentConsumptionInform(AID aid, String msg) {
        float kwh = Integer.parseInt( msg.replace("consuming=", "") );

        // Update the current consumption knowledge of this appliance
        ApplianceConsumption thisAppliance = currentApplianceConsumption.get(aid.getLocalName());

        // Agent has not yet registered
        if(thisAppliance == null) {
            registerAppliance(aid);
            thisAppliance = currentApplianceConsumption.get(aid.getLocalName());
        }

        thisAppliance.consuming = kwh;
    }

    private void registerAppliance(AID aid) {
        // Create currently consuming AND history
        currentApplianceConsumption.put(aid.getLocalName(), new ApplianceConsumption(0));
        applianceConsumptionHistory.put(aid.getLocalName(), new ApplianceConsumptionHistory());
    }

    private void updateApplianceConsumptionHistory() {
        Iterator it = currentApplianceConsumption.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            String agentIdentifier = (String) pair.getKey();
            ApplianceConsumption applianceConsumption = (ApplianceConsumption) pair.getValue();

            ApplianceConsumptionHistory ach = applianceConsumptionHistory.get(agentIdentifier);
            ach.history.add(new ApplianceConsumption(applianceConsumption.consuming));
        }
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.HomeAgent;
    }
    
    
    }

