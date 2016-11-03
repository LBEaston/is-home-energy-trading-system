package agents;

import agents.interfaces.Destroyable;
import agents.interfaces.Observable;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import ui.containers.StatusContainerBase;
import ui.interfaces.Informable;

import java.time.DayOfWeek;
import java.util.Vector;

/**
 * Created by Aswin Lakshman on 15/09/2016.
 */
public abstract class AbstractAgent extends Agent implements Observable, Destroyable {
	static int APP_TICK = 1000;

    private Vector<Informable> statusEventListeners;

    // Timekeeping
    protected int weeksElapsed = 0;
    protected DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    protected int hourOfDay = 0;
    protected int appTicksElapsed = 0;

    public void addStatusEventListener(Informable listener) {
        if(statusEventListeners == null) {
            statusEventListeners = new Vector<Informable>();
        }

        statusEventListeners.add(listener);
    }

    @Override
    public String getAgentGroup() {
        return null;
    }

    public void fireStatusChangedEvent(StatusContainerBase newStatus) {
        if(statusEventListeners == null || statusEventListeners.isEmpty()) return;

        for(Informable listener : statusEventListeners) {
            listener.inform(newStatus);
        }
    }

    public AbstractAgent() {
        registerO2AInterface(Observable.class, this);
        registerO2AInterface(Destroyable.class, this);
    }

    @Override
    public void destroy() {
        System.out.println(this.getLocalName() + " (" + this.getClass().getName() + ") - STOPPING");
        doDelete();
    }

    @Override
    protected void setup() {
        super.setup();

        configureBehaviours();
        configureAppTicker();

        System.out.println(this.getLocalName() + " (" + this.getClass().getName() + ") - STARTING");
    }

    private void configureAppTicker() {
        addBehaviour(new TickerBehaviour(this, APP_TICK) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void onTick() {
                appTicksElapsed++;
                updateDayAndHour();
                appTickElapsed();
            }
        });
    }

    private void updateDayAndHour() {
        hourOfDay++;

        if(hourOfDay > 23 ) {
            hourOfDay = 0;
            dayOfWeek = incrementDayOfWeek(dayOfWeek);

            if(dayOfWeek == DayOfWeek.MONDAY) weeksElapsed++;
        }
    }

    protected static DayOfWeek incrementDayOfWeek(DayOfWeek dow) {
        switch (dow) {
            case MONDAY:
                return DayOfWeek.TUESDAY;
            case TUESDAY:
                return DayOfWeek.WEDNESDAY;
            case WEDNESDAY:
                return DayOfWeek.THURSDAY;
            case THURSDAY:
                return DayOfWeek.FRIDAY;
            case FRIDAY:
                return DayOfWeek.SATURDAY;
            case SATURDAY:
                return DayOfWeek.SUNDAY;
            case SUNDAY:
                return DayOfWeek.MONDAY;
        }

        throw new IndexOutOfBoundsException();
    }

    // Child classes to implement this method as required
    protected void appTickElapsed() { }

    protected void configureBehaviours() {}
}
