package agents;

import agents.models.Proposal;
import agents.models.RetailerDescriptor;
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
 * Created by Aswin Lakshman on 7/09/2016.
 */
public class RetailerAgent extends AbstractAgent {
	private RetailerDescriptor descriptor;
	private Vector<Proposal> currentProposalStrategies;

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

        currentProposalStrategies = new Vector<>();
        evaluatePeakOffPeakPeriod();
    }

    // Control Logic
    @Override
    public void appTickElapsed() {
        evaluatePeakOffPeakPeriod();

        fireStatusChangedEvent(new RetailerStatusContainer(hourOfDay, dayOfWeek, currentProposalStrategies));
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

        if(currentProposalStrategies.size() == 0) currentProposalStrategies = getProposalStrategies();

        for(Proposal p : currentProposalStrategies) {
            proposalMessage += p.toString() + '|';
        }

        return proposalMessage.substring(0, proposalMessage.length() - 1);
    }

    private void evaluatePeakOffPeakPeriod() {
        int ticksThatShouldHaveElapsed;

        descriptor.currentPeriodTickCount++;

        if(descriptor.isOffPeak) {
            ticksThatShouldHaveElapsed = 24 - descriptor.peakTickCount;
        } else {
            ticksThatShouldHaveElapsed = descriptor.peakTickCount;
        }

        if(descriptor.currentPeriodTickCount >= ticksThatShouldHaveElapsed) {
        	descriptor.isOffPeak = !descriptor.isOffPeak;
        	descriptor.currentPeriodTickCount = 0;

            // Period has changed, lets re-evaluate our strategies for a bit of randomness
            currentProposalStrategies = getProposalStrategies();
        }
    }

    // Proposal Generation Logic - Now done once per duration conclusion
    // It was initially designed so that multiple proposals (for negotiation) can be generated
    // however for simplicity, we are only going to use one
    private Random random = new Random();
    private double getRandXto1(double x)
    {
        return random.nextDouble()*(1.0-x) + x;
    }

    private Vector<Proposal> getProposalStrategies() {
        Vector<Proposal> proposalStrategies = new Vector();

        // Simple proposal strategy
        // Any complex logic on variables rates/lock in periods should go here
        int appTicksRemainingInCurrentPeakOffPeakPeriod;

        if(descriptor.isOffPeak) {
            appTicksRemainingInCurrentPeakOffPeakPeriod = (24 - descriptor.peakTickCount) - descriptor.currentPeriodTickCount;
        } else {
            appTicksRemainingInCurrentPeakOffPeakPeriod = descriptor.peakTickCount - descriptor.currentPeriodTickCount;
        }

        double sellPrice = descriptor.isOffPeak
                ? descriptor.offPeakSellPrice * getRandXto1(0.8)
                : descriptor.peakSellPrice * getRandXto1(0.8);

        double buyPrice = descriptor.isOffPeak
                ? descriptor.offPeakBuyPrice * getRandXto1(0.8)
                : (descriptor.peakBuyPrice * getRandXto1(0.8));

        proposalStrategies.add(new Proposal(this.getLocalName(), sellPrice, buyPrice, appTicksRemainingInCurrentPeakOffPeakPeriod));

        return proposalStrategies;
    }
}
