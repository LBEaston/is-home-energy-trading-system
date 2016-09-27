package agents;

import agents.interfaces.ApplianceAgent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Created by fegwin on 7/09/2016.
 */
public class CyclicalApplianceAgent extends SimpleApplianceAgent implements ApplianceAgent {
    protected boolean currentlyOn = false;

    @Override
    public int currentlyConsuming() {
        return currentlyOn ? consumptionValue : 0;
    }

    @Override
    public boolean isActive() {
        return currentlyOn;
    }

    @Override
    protected void configureBehaviours() {
        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                currentlyOn = !currentlyOn;

                addBehaviour(getInformBehaviour());
            }
        });
    }
}
