import agents.*;
import agents.models.ApplianceProfile;
import agents.models.DayUsageProfile;
import agents.models.SampleUsagePoint;
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

    public static SampleUsagePoint[] makeUsageSinWave(int startTime, int endTime, double peak)
    {
	    double period = (endTime - startTime);
	    SampleUsagePoint[] result = new SampleUsagePoint[endTime-startTime+1];
	    for(int i = startTime; i <= endTime; ++i)
	    {
	    	double value = peak * Math.sin((Math.PI/period) * (double)(i-startTime));
	    	result[i-startTime] = new SampleUsagePoint(i, i+1, value);
	    }
	    return result;
    }
    
    private static Vector<ApplianceProfile> getApplianceAgentProfiles() {
        Vector<ApplianceProfile> applianceProfiles = new Vector<ApplianceProfile>();
        
        SampleUsagePoint[] solarSineWave = makeUsageSinWave(6, 18, -4.0);
        
        // Solar Panel System
        applianceProfiles.add(new ApplianceProfile("SolarPanelSystem", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, solarSineWave, 0.8, 1.1),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, solarSineWave, 0.8, 1.1)
        }));

        // Washing Machine
        applianceProfiles.add(new ApplianceProfile("WashingMachine", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.5)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(13, 14, 0.5),
                        new SampleUsagePoint(14, 15, 0.5)
                })
        }));
        
        // Dryer
        applianceProfiles.add(new ApplianceProfile("Dryer", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 3.4)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(13, 14, 3.4),
                        new SampleUsagePoint(14, 15, 3.4)
                })
        }));
        
        // DesktopComputerSystem1
        applianceProfiles.add(new ApplianceProfile("DesktopComputerSystem1", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(20, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(18, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(19, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(19, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(20, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(12, 17, 1.12),
                		new SampleUsagePoint(19, 22, 1.12)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(11, 14, 1.12),
                		new SampleUsagePoint(15, 22, 1.12)
                })
        }));

        // Clock Radio
        applianceProfiles.add(new ApplianceProfile("ClockRadio", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                })
        }));
        
        
        // LCD TV
        applianceProfiles.add(new ApplianceProfile("LCDTV", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(8, 9, 0.213),
                        new SampleUsagePoint(18, 20, 0.213)
                        
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.213)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.213)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(18, 19, 0.213),
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.213),
                        new SampleUsagePoint(18, 20, 0.213)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 10, 0.213),
                        new SampleUsagePoint(18, 20, 0.213)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 11, 0.213),
                        new SampleUsagePoint(14, 15, 0.213)
                })
        }));
        
     // Fridge/Freezer
        applianceProfiles.add(new ApplianceProfile("FridgeFreezer", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.059)
                })
        }));
        
     // Dishwasher
        applianceProfiles.add(new ApplianceProfile("Dishwasher", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(19, 20, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(12, 13, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.4)
                })
        }));
   
        // Oven
        applianceProfiles.add(new ApplianceProfile("Oven", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(20, 22, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(19, 20, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 2.5)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 2.5)
                })
        }));
        return applianceProfiles;
    }
}
