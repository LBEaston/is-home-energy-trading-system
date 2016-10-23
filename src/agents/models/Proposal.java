package agents.models;

import java.util.Vector;

/**
 * Created by fegwin on 12/10/2016.
 */
public class Proposal {
    public float sellingPrice;
    public float buyingPrice;
    public int duration;

    public Proposal(float sellingPrice, float buyingPrice, int duration) {
        this.sellingPrice = sellingPrice;
        this.buyingPrice = buyingPrice;
        this.duration = duration;

    }

    @Override
    /*proposal;sellingAt=<FLOAT>;buyingAt=<FLOAT>;duration=<INT>*/
    public String toString() {
        return String.format("proposal;sellingAt=%s;buyingAt=%s;duration=%s", sellingPrice, buyingPrice, duration);
    }

    public static Vector<Proposal> fromCompoundString(String compundProposalString) {
        Vector<Proposal> proposals = new Vector<Proposal>();
        String[] proposalStrings = compundProposalString.split("\\|");

        for(String pString : proposalStrings) {
            if(!pString.contains("proposal;")) continue;
            proposals.add(Proposal.fromString(pString));
        }

        return proposals;
    }

    public static Proposal fromString(String singleProposalString) {
        String[] parts = singleProposalString.split(";");

        return new Proposal(
                Float.parseFloat(parts[1].replace("sellingAt=", "")),
                Float.parseFloat(parts[2].replace("buyingAt=", "")),
                Integer.parseInt(parts[3].replace("duration=", ""))
        );
    }
}
