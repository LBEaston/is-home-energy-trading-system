package agents;

/**
 * Created by fegwin on 12/10/2016.
 */
public class Proposal {
    protected float sellingPrice;
    protected float buyingPrice;
    protected int duration;

    public Proposal(float sellingPrice, float buyingPrice, int duration) {
        this.sellingPrice = sellingPrice;
        this.buyingPrice = buyingPrice;
        this.duration = duration;
    }

    @Override
    /*sellingAt=<FLOAT>;buyingAt=<FLOAT>;duration=<INT>*/
    public String toString() {
        return String.format("sellingAt=%s;buyingAt=%s;duration=%s", sellingPrice, buyingPrice, duration);
    }
}
