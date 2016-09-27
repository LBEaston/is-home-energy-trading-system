package agents;

import agents.interfaces.ApplianceAgent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Created by fegwin on 7/09/2016.
 */
public class CyclicalApplianceAgent extends AbstractAgent implements ApplianceAgent {
    private boolean currentlyOn = false;

    @Override
    public int currentlyConsuming() {
        return currentlyOn ? 10 : 0;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void setup() {
        super.setup();

        this.addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                currentlyOn = !currentlyOn;

                fireStatusChangedEvent(currentlyConsuming());
            }
        });
    }

    @Override
    protected void configureBehaviours() {

    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.ApplianceAgent;
    }
}
