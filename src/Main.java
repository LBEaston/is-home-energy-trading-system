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

    public static Vector<SampleUsagePoint> makeUsageSinWave(int startTime, int endTime, double peak)
    {
    	Vector<SampleUsagePoint> result = new Vector<SampleUsagePoint>();
	    double period = (endTime - startTime);
	    for(int i = startTime; i <= endTime; ++i)
	    {
	    	double value = peak * Math.sin((Math.PI/period) * (double)(i-startTime));
	    	result.add(new SampleUsagePoint(i, i+1, value));
	    }
	    return result;
    }
    
    private static Vector<ApplianceProfile> getApplianceAgentProfiles() {
        Vector<ApplianceProfile> applianceProfiles = new Vector<ApplianceProfile>();
        
        Vector<SampleUsagePoint> ap = makeUsageSinWave(6, 18, -4.0);
        SampleUsagePoint[] app = new SampleUsagePoint[ap.size()];
        
        ap.toArray(app);
        
        // Solar Panel System
        applianceProfiles.add(new ApplianceProfile("SolarPanelSystem", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, app),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, app),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, app),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, app),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, app),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, app),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, app)
        }));

/*        // Washing Machine
        applianceProfiles.add(new ApplianceProfile("WashingMachine", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));

        // Dryer
        applianceProfiles.add(new ApplianceProfile("Dryer", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));

        // Desktop Computer System
        applianceProfiles.add(new ApplianceProfile("DesktopComputerSystem", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));

        // Clock Radio
        applianceProfiles.add(new ApplianceProfile("ClockRadio", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));

        // LCD TV
        applianceProfiles.add(new ApplianceProfile("LCDTV", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));

     // Fridge/Freezer
        applianceProfiles.add(new ApplianceProfile("FridgeFreezer", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));
        
     // Dishwasher
        applianceProfiles.add(new ApplianceProfile("Dishwasher", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(10, 17, 6)
                })
        }));
    */    
        return applianceProfiles;
    }
}
