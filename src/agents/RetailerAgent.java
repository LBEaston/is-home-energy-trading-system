package agents;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.security.InvalidParameterException;

/**
 * Created by fegwin on 7/09/2016.
 */
public class RetailerAgent extends AbstractAgent {
    private boolean isOffPeak;
    private int offPeakTickCount;
    private int peakTickCount;
    private int peakPrice;
    private int offPeakPrice;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args.length < 5) throw new InvalidParameterException("Have not provided starting consumption value");

        isOffPeak = (boolean)args[0];
        offPeakTickCount = (int)args[1];
        peakTickCount = (int)args[2];
        peakPrice = (int)args[3];
        offPeakPrice = (int)args[4];
    }

    public void configureBehaviours() {
        // Internal state stuff

        // Negotiation stuff
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        addBehaviour(new ContractNetResponder(this, template) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                /*
                 * NOTE(Lachlan 5-10-16) proposed message format for contract proposals
                 * "{id=<INT>;sellingAt=<FLOAT>;buyingAt=<FLOAT>;duration=<FLOAT>}{...}{...}"
                 * 
                 */
                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent("Yay I'm proposing");
                fireStatusChangedEvent("Proposing to " + cfp.getSender().getLocalName());

                return propose;
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
                    fireStatusChangedEvent("YAYYYYY :D " + cfp.getSender().getLocalName() + " accepted");
                    ACLMessage inform = accept.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
            }

            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                fireStatusChangedEvent("Now crying in room :( Eating ice cream because of " + cfp.getSender().getLocalName());
            }
        });
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.RetailerAgent;
    }
}
