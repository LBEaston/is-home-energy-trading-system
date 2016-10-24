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
 * Created by fegwin on 15/09/2016.
 */
public abstract class AbstractAgent extends Agent implements Observable, Destroyable {
	private static final long serialVersionUID = 1L;

	static final int APP_TICK = 1000;

    private Vector<Informable> statusEventListeners;

    // Timekeeping
    protected DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    protected int hourOfDay = 0;
    protected int appTicksElapsed = 0;

    public void addStatusEventListener(Informable listener) {
        if(statusEventListeners == null) {
            statusEventListeners = new Vector<Informable>();
        }

        statusEventListeners.add(listener);
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
            incrementDayOfWeek();
            dayOfWeek = incrementDayOfWeek(dayOfWeek);
        }
    }

    private void incrementDayOfWeek() {
        switch (dayOfWeek) {
    protected static DayOfWeek incrementDayOfWeek(DayOfWeek dow) {
        switch (dow) {
            case MONDAY:
                dayOfWeek = DayOfWeek.TUESDAY;
                break;
                return DayOfWeek.TUESDAY;
            case TUESDAY:
                dayOfWeek = DayOfWeek.WEDNESDAY;
                break;
                return DayOfWeek.WEDNESDAY;
            case WEDNESDAY:
                dayOfWeek = DayOfWeek.THURSDAY;
                break;
                return DayOfWeek.THURSDAY;
            case THURSDAY:
                dayOfWeek = DayOfWeek.FRIDAY;
                break;
                return DayOfWeek.FRIDAY;
            case FRIDAY:
                dayOfWeek = DayOfWeek.SATURDAY;
                break;
                return DayOfWeek.SATURDAY;
            case SATURDAY:
                dayOfWeek = DayOfWeek.SUNDAY;
                break;
                return DayOfWeek.SUNDAY;
            case SUNDAY:
                dayOfWeek = DayOfWeek.MONDAY;
                break;
                return DayOfWeek.MONDAY;
        }

        throw new IndexOutOfBoundsException();
    }

    // Child classes to implement this method as required
    protected void appTickElapsed() { }

    protected void configureBehaviours() {}
}
