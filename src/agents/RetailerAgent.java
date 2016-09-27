package agents;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

/**
 * Created by fegwin on 7/09/2016.
 */
public class RetailerAgent extends AbstractAgent {
    protected void setup() {
        super.setup();

        // Configure behaviours
        configureBehaviours();
    }

    public void configureBehaviours() {
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        addBehaviour(new ContractNetResponder(this, template) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                boolean shouldIPropose = testMethodRemoveMe() > 2;

                if(shouldIPropose) {
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent("Yay I'm proposing");
                    fireStatusChangedEvent("Proposing to " + cfp.getSender().getLocalName());
                    return propose;
                } else {
                    fireStatusChangedEvent("Rejecting cfp from " + cfp.getSender().getLocalName());
                    throw new RefuseException("We should just be friends. Soz");
                }
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
                if (testMethodRemoveMe() > 2) {
                    fireStatusChangedEvent("YAYYYYY :D " + cfp.getSender().getLocalName() + " accepted");
                    ACLMessage inform = accept.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                }
                else {
                    fireStatusChangedEvent("wtf happened? " + cfp.getSender().getLocalName());
                    throw new FailureException("unexpected-error");
                }
            }

            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                fireStatusChangedEvent("Now crying in room :( Eating ice cream because of " + cfp.getSender().getLocalName());
            }
        });
    }

    private int testMethodRemoveMe() {

        return 10;
    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.RetailerAgent;
    }
}
