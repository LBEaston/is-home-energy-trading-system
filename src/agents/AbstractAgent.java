package agents;

import agents.interfaces.Destroyable;
import agents.interfaces.Observable;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import ui.StatusContainerBase;
import ui.interfaces.Informable;

import java.time.DayOfWeek;
import java.util.Vector;

/**
 * Created by fegwin on 15/09/2016.
 */
public abstract class AbstractAgent extends Agent implements Observable, Destroyable {
    private Vector<Informable> statusEventListeners;

    // Timekeeping
    protected DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    protected int hourOfDay = 0;
    protected int appTicksElapsed = 0;

    public void addStatusEventListener(Informable listener) {
        if(statusEventListeners == null) {
            statusEventListeners = new Vector();
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
        addBehaviour(new TickerBehaviour(this, 10000) {
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
        }
    }

    private void incrementDayOfWeek() {
        switch (dayOfWeek) {
            case MONDAY:
                dayOfWeek = DayOfWeek.TUESDAY;
                break;
            case TUESDAY:
                dayOfWeek = DayOfWeek.WEDNESDAY;
                break;
            case WEDNESDAY:
                dayOfWeek = DayOfWeek.THURSDAY;
                break;
            case THURSDAY:
                dayOfWeek = DayOfWeek.FRIDAY;
                break;
            case FRIDAY:
                dayOfWeek = DayOfWeek.SATURDAY;
                break;
            case SATURDAY:
                dayOfWeek = DayOfWeek.SUNDAY;
                break;
            case SUNDAY:
                dayOfWeek = DayOfWeek.MONDAY;
                break;
        }
    }

    // Child classes to implement this method as required
    protected void appTickElapsed() { }

    protected abstract void configureBehaviours();
}
