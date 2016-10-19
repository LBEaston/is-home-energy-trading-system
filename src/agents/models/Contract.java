package agents.models;

import jade.lang.acl.ACLMessage;

public class Contract extends Proposal {


	/*TODO(Lachlan 28-9-16) consider putting setup fee and or minimum kWh to buy */

	/* NOTE(Lachlan 4-10-16) below used by home agent when finding best proposal */
	/* TODO(Lachlan 19-10-16) Remove this class completely, it's nothing but redundant and makes my head hurt */
	public ACLMessage associatedMessage;

	public Contract(ACLMessage associatedMessage, Proposal proposal) {
		super(proposal.sellingPrice, proposal.buyingPrice, proposal.duration);

		this.associatedMessage = associatedMessage;
	}
}
