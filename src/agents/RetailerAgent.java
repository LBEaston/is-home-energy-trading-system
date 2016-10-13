package agents;

import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.security.InvalidParameterException;
import java.util.Vector;

/**
 * Created by fegwin on 7/09/2016.
 */
public class RetailerAgent extends AbstractAgent {
    private boolean isOffPeak;

    private int offPeakTickCount;
    private int peakTickCount;

    private int peakSellPrice;
    private int offPeakSellPrice;
    private int peakBuyPrice;
    private int offPeakBuyPrice;

    private int currentPeakOffPeakTickCount = 0;

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.RetailerAgent;
    }

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length < 7) throw new InvalidParameterException("Have not provided starting consumption value");

        isOffPeak = (boolean)args[0];

        offPeakTickCount = (int)args[1];
        peakTickCount = (int)args[2];

        peakSellPrice = (int)args[3];
        peakBuyPrice = (int)args[4];

        offPeakSellPrice = (int)args[5];
        offPeakBuyPrice = (int)args[6];
    }

    // Control Logic
    @Override
    public void appTickElapsed() {
        evaluatePeakOffPeakPeriod();
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
        for(Proposal p : getProposalStrategies()) {
            proposalMessage += p.toString() + '|';
        }

        return proposalMessage.substring(0, proposalMessage.length() - 1);
    }

    private Vector<Proposal> getProposalStrategies() {
        Vector<Proposal> proposalStrategies = new Vector();

        // Simple proposal strategy
        // Any complex logic on variables rates/lock in periods should go here
        int appTicksRemainingInCurrentPeakOffPeakPeriod;

        if(isOffPeak) {
            appTicksRemainingInCurrentPeakOffPeakPeriod = offPeakTickCount - currentPeakOffPeakTickCount;
        } else {
            appTicksRemainingInCurrentPeakOffPeakPeriod = peakTickCount - currentPeakOffPeakTickCount;
        }

        proposalStrategies.add(new Proposal(isOffPeak ? offPeakSellPrice : peakSellPrice,
                isOffPeak ? offPeakBuyPrice : peakBuyPrice,
                appTicksRemainingInCurrentPeakOffPeakPeriod));

        return proposalStrategies;
    }

    private void evaluatePeakOffPeakPeriod() {
        int ticksThatShouldHaveElapsed;

        if(isOffPeak) {
            ticksThatShouldHaveElapsed = offPeakTickCount;
        } else {
            ticksThatShouldHaveElapsed = peakTickCount;
        }

        if(currentPeakOffPeakTickCount >= ticksThatShouldHaveElapsed) {
            isOffPeak = !isOffPeak;
            currentPeakOffPeakTickCount = 0;
        }
    }
}
