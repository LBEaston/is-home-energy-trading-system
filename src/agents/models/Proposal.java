package agents.models;

import java.util.Vector;

/**
 * Created by Aswin Lakshman on 12/10/2016.
 */
public class Proposal {
    public String retailer;
    public double retailerSellingPrice;
    public double retailerBuyingPrice;
    public int duration;

    public Proposal(String retailer, double sellingPrice, double buyingPrice, int duration) {
        this.retailer = retailer;

        this.retailerSellingPrice = sellingPrice;
        this.retailerBuyingPrice = buyingPrice;
        this.duration = duration;

    }

    @Override
    /*proposal;sellingAt=<FLOAT>;buyingAt=<FLOAT>;duration=<INT>*/
    public String toString() {
        return String.format("proposal;seller=%s;sellingAt=%s;buyingAt=%s;duration=%s", retailer, retailerSellingPrice, retailerBuyingPrice, duration);
    }

    public String toReadableString() {
        return String.format("Sale Price (%s) | Buy Price (%s) | Duration (%s)", retailerSellingPrice, retailerBuyingPrice, duration);
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
                parts[1].replace("seller=", ""),
                Float.parseFloat(parts[2].replace("sellingAt=", "")),
                Float.parseFloat(parts[3].replace("buyingAt=", "")),
                Integer.parseInt(parts[4].replace("duration=", ""))
        );
    }
}
