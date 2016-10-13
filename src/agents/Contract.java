package agents;

import jade.lang.acl.ACLMessage;

public class Contract extends Proposal {


	/*TODO(Lachlan 28-9-16) consider putting setup fee and or minimum kWh to buy */

	/* NOTE(Lachlan 4-10-16) below used by home agent when finding best proposal */
	public ACLMessage associatedMessage;
	public float predictedExpenditurePerHour;

	public Contract(ACLMessage associatedMessage, Proposal proposal) {
		super(proposal.sellingPrice, proposal.buyingPrice, proposal.duration);

		this.associatedMessage = associatedMessage;
	}
}
