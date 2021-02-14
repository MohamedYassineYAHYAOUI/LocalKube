package fr.umlv.LocalKube.logs;

import fr.umlv.LocalKube.app.Application;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * log handler class, send signal to different log observers
 */
@Component
public class Log {

    private final List<LogObserver> observers;

    private final SynchronizedBlockingBuffer<? extends LogModel> buffer;

    Log(List<LogObserver> observers, SynchronizedBlockingBuffer buffer){
        this.observers = observers;
        this.buffer = buffer;

    }

    /**
     * send signal to all the observers on creation of a new map
     * @param app new application created
     */
    public void observeNewApp(Application app) {
        Objects.requireNonNull(app);
        for(var obs : observers){
            obs.onCreateApp(app, this);
        }
    }

    /**
     * send signal to different observers to stop detecting logs for the id
     * @param id id of the application to stop detecting logs for
     */
    public void stopDetectingLogs(int id){
        for(var obs : observers){
            obs.onStop(id);
        }
    }

    /**
     * send signal for different observers that local kube is shutting down
     */
    public void shutDownApp(){
        for(var obs : observers){
            obs.onShutDown();
        }
    }

    /**
     * getter for the synchronized buffer
     * @return the synchronized buffer
     */
    SynchronizedBlockingBuffer getBuffer(){
        return buffer;
    }

    /**
     * observer methode, send signal to start detecting logs
     */
    void detectLogs(){
        for(var obs : observers){
            obs.onStart(this);
        }
    }

}
