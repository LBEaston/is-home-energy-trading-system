package agents;

import agents.interfaces.ApplianceAgent;

/**
 * Created by fegwin on 7/09/2016.
 */
public class CyclicalVariableConsumptionApplianceAgent extends AbstractAgent implements ApplianceAgent {
    @Override
    public int currentlyConsuming() {
        return 0;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void setup() {
        super.setup();
    }

    @Override
    protected void configureBehaviours() {

    }

    @Override
    public EnergyAgentType getAgentType() {
        return EnergyAgentType.ApplianceAgent;
    }
}
