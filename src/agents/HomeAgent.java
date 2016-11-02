package agents;

import agents.models.ApplianceConsumption;
import agents.models.ApplianceConsumptionHistory;
import agents.models.InstantDescriptor;
import agents.models.Proposal;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import ui.containers.HomeStatusContainer;

import java.util.Vector;
import java.util.*;

/**
 * Created by Aswin Lakshman on 7/09/2016.
 */
public class HomeAgent extends AbstractAgent {
    // Internal State Variables
    private Vector<String> retailers;
    private HashMap<String, ApplianceConsumptionHistory> applianceConsumptionHistory;

    private HashMap<String, ApplianceConsumption> currentApplianceConsumption;

    private Proposal currentEnergyContract = null;
    private double totalSpendToDate = 0;
    private int ticksTillNextNegotiation = 0;

    private boolean inLoyalMode = true;
    private boolean hasLoyalContract = false;
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

    @Override
    public String getAgentGroup() {
        return getLocalName();
    }

//    private GraphPanel graphPrediction;
//    private double[] graphScoresPrediction = new double[24*7];
    protected void setup() {
        super.setup();

        // Grab the known retailers, from args
        Object[] args = (Object[])getArguments()[0];

        for(Object arg : args) {
            String retailer = (String)arg;
            retailers.add(retailer);
        }

        this.inLoyalMode = (boolean)getArguments()[1];

//        graphPrediction = new GraphPanel(graphScoresPrediction);
//        graphPrediction.setPreferredSize(new Dimension(1000, 200));
//        JFrame predictionFrame = new JFrame("Prediction Graph");
//        predictionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        predictionFrame.getContentPane().add(graphPrediction);
//        predictionFrame.pack();
//        predictionFrame.setLocationRelativeTo(null);
//        predictionFrame.setVisible(true);
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

        //graphPrediction.setScores(graphScoresPrediction);
    }

    private void negotiateWithRetailers() {
        if(inTheMiddleOfANegotiation || hasLoyalContract) return;

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
                for(ACLMessage msg : proposalMessages)
                {
                    String compoundProposalString = msg.getContent();
                    Vector<Proposal> proposalsFromAgent = Proposal.fromCompoundString(compoundProposalString);

                    for(Proposal p : proposalsFromAgent)
                    {
                    	double currentUtility = getUtilityOfContract(p);
                    	if (currentUtility > bestUtility)
                    	{
	                    	bestProposal = p;
	                        bestUtility = currentUtility;
	                        bestMessage = msg;
                    	}
                    }
                }

                if (bestMessage != null)
                {
	                // Accept best proposal
	                ACLMessage reply = bestMessage.createReply();
	
	                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
	                reply.setContent(bestProposal.toString());
	
	                acceptances.addElement(reply);
                }
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

                if(inLoyalMode) {
                    hasLoyalContract = true;
                    // Stop trying to get another contract. This is it!
                }
            }
        };
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

                    updateTotalSpend();
                    updateApplianceConsumptionHistory();

                    fireStatusChangedEvent(new HomeStatusContainer(getCurrentNetConsumption(), totalSpendToDate, hourOfDay, dayOfWeek, currentEnergyContract));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void updateTotalSpend() {
        if(currentEnergyContract == null) return;

        double currentBuyRate = currentEnergyContract.retailerSellingPrice;
        double currentSellingRate = currentEnergyContract.retailerBuyingPrice;

        double currentConsumption = getCurrentNetConsumption();

        if(currentConsumption > 0) {
            totalSpendToDate += currentConsumption * currentBuyRate;
        } else {
            totalSpendToDate += currentConsumption * currentSellingRate;
        }
    }

    /** Prediction Logic **/
    private double predictKWHForNextNHours(int n) {
        double historicPrediction = doHistoricPrediction(n);
        double linearPrediction = doLinearPrediction(n);

        if(Double.isNaN(historicPrediction)) return linearPrediction;

        return historicPrediction;
    }

    private double doHistoricPrediction(int duration) {
        Vector<InstantDescriptor> instancesForTheNextNHoursWorthOfPredictions = getHourOfDayInstancesForNextNHours(duration);

        Vector<Double> predictedConsumptionsForTheNextNHours = new Vector<Double>();

        for(InstantDescriptor instantDescriptor : instancesForTheNextNHoursWorthOfPredictions) {
            double historicalAverageForThisHourDay = getHistoricalConsumptionTotalByInstantDescriptor(instantDescriptor);
            predictedConsumptionsForTheNextNHours.add(historicalAverageForThisHourDay);
            //graphScoresPrediction[(instantDescriptor.dayOfWeek.getValue()-1) *24 + instantDescriptor.hourOfDay] = (historicalAverageForThisHourDay);
        }

        double sum = 0;
        for(Double val : predictedConsumptionsForTheNextNHours) {
            sum += val;
        }

        return sum;
    }

    private double getHistoricalConsumptionTotalByInstantDescriptor(InstantDescriptor instantDescriptor) {
        Vector<Double> dataPoints = new Vector<Double>();

        Iterator<Map.Entry<String, ApplianceConsumptionHistory>> it = applianceConsumptionHistory.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, ApplianceConsumptionHistory> pair = it.next();

            ApplianceConsumptionHistory history = pair.getValue();

            for(ApplianceConsumption ac : history.history) {
                if(ac.hourOfDay == instantDescriptor.hourOfDay && ac.dayOfWeek == instantDescriptor.dayOfWeek) {
                    dataPoints.add(ac.consuming);
                }
            }
        }

        if (dataPoints.size() == 0) return 0;
        double sum = 0;
        for(Double dataPoint : dataPoints) {
            sum += dataPoint;
        }

        return sum/(dataPoints.size());
    }

    private Vector<InstantDescriptor> getHourOfDayInstancesForNextNHours(int duration) {
        InstantDescriptor current = new InstantDescriptor(dayOfWeek, hourOfDay);
        Vector<InstantDescriptor> retVal = new Vector<>();

        for(int i = 0; i < duration; i++) {
            current.hourOfDay++;

            if(current.hourOfDay > 23 ) {
                current.hourOfDay = 0;
                current.dayOfWeek = incrementDayOfWeek(dayOfWeek);
            }

            retVal.add(new InstantDescriptor(current.dayOfWeek, current.hourOfDay));
        }

        return retVal;
    }

    private double doLinearPrediction(int n) {
        double[] historyX = new double[12], historyY = new double[12];
        LinearFunction predictedLine = getLinearRegression(historyX, historyY);

        double result = predictedLine.definiteIntegral(historyX[historyX.length-1], historyX[historyX.length-1] + n);

        return result;
    }

    private class LinearFunction {
    	private double _a, _b;
    	public LinearFunction(double a, double b)
    	{
    		_a = a;
    		_b = b;
    	}
    	
    	public double definiteIntegral(double low, double high)
    	{    		
    		return (0.5*_a*high*high + _b*high) - (0.5*_a*low*low + _b*low);
    	}
    }
    
    /* NOTE: Code modified form of: http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html */
    private LinearFunction getLinearRegression(double[] x, double[] y) {
        int n = 0;

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0;
        for (int i = 0; i < x.length; ++i)
        {
        	sumx  += x[n];
            sumy  += y[n];
            n++;
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double a = xybar / xxbar;
        double b = ybar - a * xbar;
        
        return new LinearFunction(a, b);
    }
    
    private double getUtilityOfContract(Proposal c) {
    	if(inLoyalMode && currentEnergyContract != null) {
            // Always favour current contract
            if(currentEnergyContract.retailer == c.retailer) {
                return 1000;
            } else {
                return 0;
            }
        }

        double result = 0;
    	double predicted_kwh = predictKWHForNextNHours(c.duration);
    	
    	double buypriceContribution = -1.0;
		double sellpriceContribution = 0.1;
		
    	if (predicted_kwh > 0.0)
    	{
    		// predicted going to consume more
    		buypriceContribution = -1.0; // Want buy price to be as low as possible
    		sellpriceContribution = 0.1; // Prefer sell price to be higher in case prediction was wrong
    	}
    	else
    	{
    		// predicted going to produced more
    		buypriceContribution = -0.1; // Care less about buy price
    		sellpriceContribution = 1.0; // Want 
    	}
    	
    	result = (buypriceContribution*c.retailerSellingPrice + sellpriceContribution*c.retailerBuyingPrice)/Math.sqrt((double)c.duration);
    	
    	return result;
    }

    public double getCurrentNetConsumption() {
        Vector<ApplianceConsumption> consumers = new Vector<ApplianceConsumption>();

        Iterator<Map.Entry<String, ApplianceConsumption>> it = currentApplianceConsumption.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, ApplianceConsumption> pair = it.next();

            ApplianceConsumption applianceConsumption = pair.getValue();
            consumers.add(applianceConsumption);
        }

        return calculateApplianceConsumption(consumers);
    }

    private double calculateApplianceConsumption(Vector<ApplianceConsumption> consumers) {
        double netConsumtion = 0;

        for(ApplianceConsumption ac : consumers) {
            netConsumtion += ac.consuming;
        }

        return netConsumtion;
    }

    /** Appliance Interaction Handlers **/
    private void handleApplianceAgentConsumptionInform(AID aid, String msg) {
        double kwh = Double.parseDouble( msg.replace("consuming=", "") );

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

