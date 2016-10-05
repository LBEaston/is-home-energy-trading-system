package agents;

import jade.lang.acl.ACLMessage;

public class Contract {
	/* NOTE(Lachlan 4-10-16) Retailer agent must fill these out */
	public float dolarsPerKWH;
	public float dolarsPerKWHBuying;
	public float durationInSeconds;
	/*TODO(Lachlan 28-9-16) consider putting setup fee and or minimum kWh to buy */
	
	
	/* NOTE(Lachlan 4-10-16) below used by home agent when finding best proposal */
	public ACLMessage associatedMessage;
	public float predictedSpendaturePerHour;
}
