import agents.*;
import agents.models.ApplianceProfile;
import agents.models.DayUsageProfile;
import agents.models.PeakUsagePeriod;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;

import jade.wrapper.*;
import ui.SmartHomeEnergyApplication;

import javax.swing.*;
import java.time.DayOfWeek;
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

        //2. Create a Sniffer
        AgentController sniffer = mainContainer.createNewAgent("mySniffer", "jade.tools.sniffer.Sniffer",
                new Object[]{"AGL;CityPower;HomeBrand;HomeAgent;SolarPanel;TV1;TV2;WashingMachine;Fridge"});
        sniffer.start();

        //3. Some agent container stuff
        Profile p = new ProfileImpl(false);
        AgentContainer agentContainer = jadeRuntime.createAgentContainer(p);

        //4. Startup all the agents
            // 3 retailers
            // 1 homeagent
                // 2 solar panel
                // 2 televisions
                // 1 washing machine
                // 1 fridge
        Vector<AgentController> agents = new Vector<>();

        /*
                isOffPeak = (boolean)args[0];
                offPeakTickCount = (int)args[1];
                peakTickCount = (int)args[2];
                peakPrice = (int)args[3];
                offPeakPrice = (int)args[4];
         */
        agents.add(agentContainer.createNewAgent("AGL", RetailerAgent.class.getName(), new Object[] {true, 8, 16, 123, 434, 78, 234}));
        agents.add(agentContainer.createNewAgent("CityPower", RetailerAgent.class.getName(), new Object[] {true, 8, 16, 123, 434, 78, 243}));
        agents.add(agentContainer.createNewAgent("HomeBrand", RetailerAgent.class.getName(), new Object[] {true, 8, 16, 123, 434, 78, 243}));

        agents.add(agentContainer.createNewAgent("HomeAgent", HomeAgent.class.getName(), new Object[] {"HomeBrand", "AGL", "CityPower"}));

        Vector<ApplianceProfile> applianceProfiles = getApplianceAgentProfiles();

        for(ApplianceProfile ap : applianceProfiles) {
            agents.add(agentContainer.createNewAgent(ap.applianceName, ApplianceAgent.class.getName(), new Object[] {"HomeAgent", ap}));
        }

        // Starting our agents
        for(AgentController agent : agents) {
            agent.start();
        }

        //5. Fire up our user interface
        SmartHomeEnergyApplication smartHomeEnergyApplicationUi = new SmartHomeEnergyApplication(agents);
        SwingUtilities.invokeLater(smartHomeEnergyApplicationUi);
    }

    private static Vector<ApplianceProfile> getApplianceAgentProfiles() {
        Vector<ApplianceProfile> applianceProfiles = new Vector();

        // Solar Panel 1
        applianceProfiles.add(new ApplianceProfile("SolarPanel1", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                })
        }));

        // Solar Panel 2
        applianceProfiles.add(new ApplianceProfile("SolarPanel2", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, -453, -234, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                })
        }));

        // Tv 1
        applianceProfiles.add(new ApplianceProfile("TV1", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 45, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 45, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 45, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 45, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 45, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 45, 45, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 45, 45, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                })
        }));

        // Tv 1
        applianceProfiles.add(new ApplianceProfile("TV2", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 78, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 78, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 78, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 78, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 78, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 78, 78, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 78, 78, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 17, 6)
                })
        }));

        // Washing Machine
        applianceProfiles.add(new ApplianceProfile("WashingMachine", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, 0, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, 0, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, 0, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, 0, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, 0, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.SATURDAY, 168, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 13, 2)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 168, 0, new PeakUsagePeriod[] {
                        new PeakUsagePeriod(10, 13, 2)
                })
        }));

        // Fridge
        applianceProfiles.add(new ApplianceProfile("Fridge", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, 1400, new PeakUsagePeriod[] {}),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, 1400, new PeakUsagePeriod[] {})
        }));

        return applianceProfiles;
    }
}
