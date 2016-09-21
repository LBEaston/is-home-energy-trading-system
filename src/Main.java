import agents.*;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;

import jade.wrapper.*;

import javax.swing.*;
import java.util.Vector;

/**
 * Created by fegwin on 7/09/2016.
 */
public class Main {
    public static void main(String[] args) throws StaleProxyException {
        //1. Open up the agent management screen (just because we probably should)
        Runtime jadeRuntime = Runtime.instance();

        Profile pMain = new ProfileImpl(null, 8888, null);
        pMain.setParameter(Profile.GUI, "true");
        ContainerController mainContainer = jadeRuntime.createMainContainer(pMain);

        //2. Some agent container stuff
        Profile p = new ProfileImpl(false);
        AgentContainer agentContainer = jadeRuntime.createAgentContainer(p);

        //3. Startup all the agents
            // 3 retailers
            // 1 homeagent
                // 1 solar panel
                // 2 televisions
                // 1 washing machine
                // 1 fridge
        Vector<AgentController> agents = new Vector<>();

        agents.add(agentContainer.createNewAgent("AGL", RetailerAgent.class.getName(), new String[] {}));
        agents.add(agentContainer.createNewAgent("CityPower", RetailerAgent.class.getName(), new String[] {}));
        agents.add(agentContainer.createNewAgent("HomeBrand", RetailerAgent.class.getName(), new String[] {}));

        agents.add(agentContainer.createNewAgent("HomeAgent", HomeAgent.class.getName(), new String[] {"HomeBrand", "AGL", "CityPower"}));

        agents.add(agentContainer.createNewAgent("SolarPanel", CyclicalApplianceAgent.class.getName(), new String[] {"HomeAgent"}));
        agents.add(agentContainer.createNewAgent("TV1", CyclicalApplianceAgent.class.getName(), new String[] {"HomeAgent"}));
        agents.add(agentContainer.createNewAgent("TV2", CyclicalApplianceAgent.class.getName(), new String[] {"HomeAgent"}));
        agents.add(agentContainer.createNewAgent("WashingMachine", CyclicalVariableConsumptionApplianceAgent.class.getName(), new String[] {"HomeAgent"}));
        agents.add(agentContainer.createNewAgent("Fridge", AlwaysOnApplianceAgent.class.getName(), new String[] {"HomeAgent"}));

        // Starting our agents
        for(AgentController agent : agents) {
            agent.start();
        }

        //2. Fire up our user interface
        SmartHomeEnergyApplication smartHomeEnergyApplicationUi = new SmartHomeEnergyApplication(agents);
        SwingUtilities.invokeLater(smartHomeEnergyApplicationUi);
    }
}
