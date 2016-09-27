package agents;

import agents.interfaces.ApplianceAgent;
import jade.core.behaviours.TickerBehaviour;

import java.security.InvalidParameterException;
import java.util.Random;

/**
 * Created by fegwin on 7/09/2016.
 */
public class CyclicalVariableConsumptionApplianceAgent extends CyclicalApplianceAgent implements ApplianceAgent {
    protected int peakConsumptionValue = 0;
    protected int offPeakConsumptionValue = 0;

    @Override
    protected void setup() {
        super.setup();

        // Get the peak consumption value as well
        Object[] args = getArguments();

        if(args.length < 4) throw new InvalidParameterException("Have not provided starting consumption value");

        int offPeakConsumptionValue = (int)args[2];
        int peakConsumptionValue = (int)args[3];

        this.peakConsumptionValue = peakConsumptionValue;
        this.offPeakConsumptionValue = offPeakConsumptionValue;
    }

    @Override
    protected void configureBehaviours() {
        addBehaviour(new TickerBehaviour(this, 20000) {
            @Override
            protected void onTick() {
                currentlyOn = !currentlyOn;
                addBehaviour(getInformBehaviour());
            }
        });

        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                reEvaluateConsumptionValue();
                addBehaviour(getInformBehaviour());
            }
        });
    }

    private void reEvaluateConsumptionValue() {
        Random r = new Random();

        consumptionValue = r.nextInt(peakConsumptionValue - offPeakConsumptionValue) + offPeakConsumptionValue;
    }
}
