package fr.umlv.LocalKube.logs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Data base observer, does specific treatment on start and on shutdown of the application
 */
@Component
class BddObserver implements LogObserver {

    @Autowired
    @Qualifier("bddThread")
    private Thread logThread;

    /**
     *observer methode, starts the data base thread on start of the application
     * @param logHandler
     */
    @Override
    public void onStart(Log logHandler){
        logThread.start();
    }

    /**
     *observer methode, send interrupt signal to the data base thread on shut down of the application
     */
    @Override
    public void onShutDown(){
        logThread.interrupt();
    }

}
