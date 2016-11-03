import agents.*;
import agents.models.ApplianceProfile;
import agents.models.DayUsageProfile;
import agents.models.RetailerDescriptor;
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
 * Created by Aswin Lakshman on 7/09/2016.
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

        //3. Create/Startup all the agents
            // retailers
            // smart homeagent
                // appliances
            // dumb homeagent
                // appliances
        Vector<AgentController> agents = new Vector<>();

        // retailers
        createAndAddRetailers(agentContainer, agents);

        // smart home agent
        createAndAddSmartHomeAgent(agentContainer, agents);

        // dumb home agent
        createAndAddDumbHomeAgent(agentContainer, agents);

        // Starting our agents
        for(AgentController agent : agents) {
            agent.start();
        }

        //4. Fire up our user interface
        SmartHomeEnergyApplication smartHomeEnergyApplicationUi = new SmartHomeEnergyApplication(agents);
        SwingUtilities.invokeLater(smartHomeEnergyApplicationUi);

        //5. Create a sniffer
        String snifferConfigString = "";
        for(AgentController ac : agents) {
            snifferConfigString += ac.getName() + ";";
        }

        snifferConfigString = snifferConfigString.substring(0, snifferConfigString.length() - 1);

        AgentController sniffer = mainContainer.createNewAgent("mySniffer", "jade.tools.sniffer.Sniffer",
                new Object[]{snifferConfigString});
        sniffer.start();

    }

    private static void createAndAddDumbHomeAgent(AgentContainer agentContainer, Vector<AgentController> agents) throws StaleProxyException {
        agents.add(agentContainer.createNewAgent("DumbHomeAgent", HomeAgent.class.getName(), new Object[] {new Object[] {"HomeBrand", "AGL", "CityPower"}, true}));

        // appliance agents
        Vector<ApplianceProfile> applianceProfiles = getApplianceAgentProfiles();

        for(ApplianceProfile ap : applianceProfiles) {
            agents.add(agentContainer.createNewAgent(ap.applianceName + "1", ApplianceAgent.class.getName(), new Object[] {"DumbHomeAgent", ap}));
        }
    }

    private static void createAndAddSmartHomeAgent(AgentContainer agentContainer, Vector<AgentController> agents) throws StaleProxyException {
        agents.add(agentContainer.createNewAgent("SmartHomeAgent", HomeAgent.class.getName(), new Object[] {new Object[] {"HomeBrand", "AGL", "CityPower"}, false}));

        // appliance agents
        Vector<ApplianceProfile> applianceProfiles = getApplianceAgentProfiles();

        for(ApplianceProfile ap : applianceProfiles) {
            agents.add(agentContainer.createNewAgent(ap.applianceName + "2", ApplianceAgent.class.getName(), new Object[] {"SmartHomeAgent", ap}));
        }
    }

    private static void createAndAddRetailers(AgentContainer agentContainer, Vector<AgentController> agents) throws StaleProxyException {
        RetailerDescriptor AGL = new RetailerDescriptor();
        AGL.isOffPeak = true;
        AGL.peakTickCount = 12;
        AGL.peakSellPrice = .43;
        AGL.offPeakSellPrice = .32;
        AGL.peakBuyPrice = .23;
        AGL.offPeakBuyPrice = .39;
        AGL.currentPeriodTickCount = 0;

        RetailerDescriptor CityPower = new RetailerDescriptor();
        CityPower.isOffPeak = false;
        CityPower.peakTickCount = 17;
        CityPower.peakSellPrice = .53;
        CityPower.offPeakSellPrice = .23;
        CityPower.peakBuyPrice = .33;
        CityPower.offPeakBuyPrice = .39;
        CityPower.currentPeriodTickCount = 0;

        RetailerDescriptor HomeBrand = new RetailerDescriptor();
        HomeBrand.isOffPeak = false;
        HomeBrand.peakTickCount = 16;
        HomeBrand.peakSellPrice = .46;
        HomeBrand.offPeakSellPrice = .33;
        HomeBrand.peakBuyPrice = .26;
        HomeBrand.offPeakBuyPrice = .62;
        HomeBrand.currentPeriodTickCount = 0;

        agents.add(agentContainer.createNewAgent("AGL", RetailerAgent.class.getName(), new Object[] {AGL}));
        agents.add(agentContainer.createNewAgent("CityPower", RetailerAgent.class.getName(), new Object[] {CityPower}));
        agents.add(agentContainer.createNewAgent("HomeBrand", RetailerAgent.class.getName(), new Object[] {HomeBrand}));
    }

    private static SampleUsagePoint[] makeUsageSinWave(int startTime, int endTime, double peak) {
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
        Vector<ApplianceProfile> applianceProfiles = new Vector();
        
        SampleUsagePoint[] solarSineWave = makeUsageSinWave(6, 18, -2.5);
        
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
                        new SampleUsagePoint(0, 0, 0.1)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {}),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 0, 0.9)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {}),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {}),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {}),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(13, 14, 1.2),
                        new SampleUsagePoint(14, 15, 1.6)
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
                        new SampleUsagePoint(14, 15, 1.2)
                })
        }));
        
        // DesktopComputerSystem1
        applianceProfiles.add(new ApplianceProfile("DesktopComputerSystem", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(20, 22, 1.52)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(18, 22, 1.32)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(19, 22, 1.92)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(19, 22, 0.92)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(20, 22, 0.87)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(12, 17, 0.72),
                		new SampleUsagePoint(19, 22, 2.12)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(11, 14, 0.62),
                		new SampleUsagePoint(15, 22, 2.52)
                })
        }));

        // Clock Radio
        applianceProfiles.add(new ApplianceProfile("ClockRadio", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.005)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.006)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.007)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.006)
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
                        new SampleUsagePoint(8, 9, 0.10),
                        new SampleUsagePoint(18, 20, 0.31)
                        
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.07)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.09)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(18, 19, 0.19),
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 9, 0.08),
                        new SampleUsagePoint(18, 20, 0.213)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 10, 0.23),
                        new SampleUsagePoint(18, 20, 0.28)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                		new SampleUsagePoint(8, 11, 0.109),
                        new SampleUsagePoint(14, 15, 0.301)
                })
        }));
        
        // Fridge/Freezer
        applianceProfiles.add(new ApplianceProfile("FridgeFreezer", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0.01, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 23, 0.059)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.15)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.123)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.078)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.102)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.19)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0.01, new SampleUsagePoint[] {
                		new SampleUsagePoint(0, 23, 0.20)
                })
        }));
        
        // Dishwasher
        applianceProfiles.add(new ApplianceProfile("Dishwasher", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 0.8)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 1.4)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.1)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(19, 20, 1.2)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 1.2)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(12, 13, 2.4)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 2.1)
                })
        }));
   
        // Oven
        applianceProfiles.add(new ApplianceProfile("Oven", new DayUsageProfile[] {
                new DayUsageProfile(DayOfWeek.MONDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(20, 22, 2.2)
                }),
                new DayUsageProfile(DayOfWeek.TUESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 2.9)
                }),
                new DayUsageProfile(DayOfWeek.WEDNESDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(0, 0, 3.5)
                }),
                new DayUsageProfile(DayOfWeek.THURSDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(19, 20, 2.2)
                }),
                new DayUsageProfile(DayOfWeek.FRIDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 2.7)
                }),
                new DayUsageProfile(DayOfWeek.SATURDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(17, 18, 3.2)
                }),
                new DayUsageProfile(DayOfWeek.SUNDAY, 0, new SampleUsagePoint[] {
                        new SampleUsagePoint(18, 19, 3.5)
                })
        }));
        return applianceProfiles;
    }
}
