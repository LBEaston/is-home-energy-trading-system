package agents;

import jade.core.behaviours.TickerBehaviour;

/**
 * Created by fegwin on 7/09/2016.
 */
public class CyclicalApplianceAgent extends AbstractAgent implements ApplianceAgent {
    private int currentlyConsuming = 0;

    @Override
    public int currentlyConsuming() {
        return currentlyConsuming;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void setup() {
        super.setup();

        this.addBehaviour(new TickerBehaviour(this, 4000) {
            @Override
            protected void onTick() {
                currentlyConsuming++;
                fireStatusChangedEvent("Now consuming " + currentlyConsuming + " ticks now");
            }
        });
    }
}
