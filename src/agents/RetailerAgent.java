package agents;

import agents.models.Proposal;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import ui.containers.RetailerStatusContainer;

import java.security.InvalidParameterException;
import java.util.Random;
import java.util.Vector;

/**
 * Created by fegwin on 7/09/2016.
 */
public class RetailerAgent extends AbstractAgent {
	private static final long serialVersionUID = 1L;

	
	/* https://www.originenergy.com.au/terms-and-conditions/qld-electricity-tariffs.html
	 * Offpeak is on weekends and between 10pm�7am on weekdays
	 * Shoulder time between 7am�4pm, 8pm�10pm
	 * 
	 * Price per kwh averages around ~25-35cents in Australia, (17-20 off peak): https://www.ovoenergy.com/guides/energy-guides/average-electricity-prices-kwh.html
	 * http://cmeaustralia.com.au/wp-content/uploads/2013/09/FINAL-INTERNATIONAL-PRICE-COMPARISON-FOR-PUBLIC-RELEASE-29-MARCH-2012.pdf
	 */
	
	public static class RetailerDescriptor
	{
		public boolean isOffPeak;

		public int offPeakTickCount;
		public int peakTickCount;

        public double peakSellPrice;
        public double offPeakSellPrice;
        public double peakBuyPrice;
        public double offPeakBuyPrice;

		public int currentPeakOffPeakTickCount = 0;
    }
	
	private RetailerDescriptor descriptor;
	
    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.RetailerAgent;
    }

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length < 1) throw new InvalidParameterException("Have not provided starting consumption value");

        descriptor = (RetailerDescriptor)args[0];
    }

    // Control Logic
    @Override
    public void appTickElapsed() {
        evaluatePeakOffPeakPeriod();

        Vector<Proposal> currentProposals = getProposalStrategies();
        fireStatusChangedEvent(new RetailerStatusContainer(hourOfDay, dayOfWeek, currentProposals));
    }

    public void configureBehaviours() {
        // Negotiation stuff
        addBehaviour(getContractNetResponderBehaviour());
    }

    public Behaviour getContractNetResponderBehaviour() {
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        return new ContractNetResponder(this, template) {
			private static final long serialVersionUID = 1L;
			@Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);

                String proposalMessage = getCurrentRatesMessage();
                propose.setContent(proposalMessage);

                return propose;
            }
            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                String acceptedProposalMessage = accept.getContent();
                Proposal acceptedProposal = Proposal.fromString(acceptedProposalMessage);

                ACLMessage inform = accept.createReply();
                inform.setContent(acceptedProposal.toString());
                inform.setPerformative(ACLMessage.INFORM);

                System.out.println("accept from " + accept.getSender().getLocalName());

                return inform;
            }
            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                System.out.println("rejection from " + reject.getSender().getLocalName());
            }
        };
    }

    // Proposal Strategies
    public String getCurrentRatesMessage() {
        String proposalMessage = "";
        for(Proposal p : getProposalStrategies()) {
            proposalMessage += p.toString() + '|';
        }

        return proposalMessage.substring(0, proposalMessage.length() - 1);
    }
    
    private Random random = new Random();
    private double getRandXto1(double x)
    {
    	return random.nextDouble()*(1.0-x) + x;
    }

    private Vector<Proposal> getProposalStrategies() {
        Vector<Proposal> proposalStrategies = new Vector<Proposal>();

        // Simple proposal strategy
        // Any complex logic on variables rates/lock in periods should go here
        int appTicksRemainingInCurrentPeakOffPeakPeriod;

        if(descriptor.isOffPeak) {
            appTicksRemainingInCurrentPeakOffPeakPeriod = descriptor.offPeakTickCount - descriptor.currentPeakOffPeakTickCount;
        } else {
            appTicksRemainingInCurrentPeakOffPeakPeriod = descriptor.peakTickCount - descriptor.currentPeakOffPeakTickCount;
        }

        double sellPrice = descriptor.isOffPeak ? descriptor.offPeakSellPrice*getRandXto1(0.8) : descriptor.peakSellPrice*getRandXto1(0.8);
        double buyPrice = descriptor.isOffPeak ? descriptor.offPeakBuyPrice*getRandXto1(0.8) : (descriptor.peakBuyPrice*getRandXto1(0.8));

        proposalStrategies.add(new Proposal(this.getLocalName(), sellPrice, buyPrice, appTicksRemainingInCurrentPeakOffPeakPeriod));

        return proposalStrategies;
    }
    
    private Proposal concedeProposal(Proposal p)
    {
		return p;
    }

    private void evaluatePeakOffPeakPeriod() {
        int ticksThatShouldHaveElapsed;

        if(descriptor.isOffPeak) {
            ticksThatShouldHaveElapsed = descriptor.offPeakTickCount;
        } else {
            ticksThatShouldHaveElapsed = descriptor.peakTickCount;
        }

        if(descriptor.currentPeakOffPeakTickCount >= ticksThatShouldHaveElapsed) {
        	descriptor.isOffPeak = !descriptor.isOffPeak;
        	descriptor.currentPeakOffPeakTickCount = 0;
        }
    }
}
