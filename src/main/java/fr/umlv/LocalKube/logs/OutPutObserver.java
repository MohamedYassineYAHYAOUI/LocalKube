package fr.umlv.LocalKube.logs;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.umlv.LocalKube.app.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

@Component
class OutPutObserver implements LogObserver{

    private final HashMap<Integer, Thread> appsOutPut = new HashMap<>();
    private final ObjectMapper objMapper = new ObjectMapper();
    @Autowired
    @Qualifier("logBuffer")
    private SynchronizedBlockingBuffer buffer;

    /**
     * add logs to the buffer if its an application log
     * @param appName application name
     * @param appId application id
     * @param log log handler
     */
    private void addOutPut(String appName, int appId, Log log){

        var thread = new Thread(()->{
            var process = new ProcessBuilder("docker", "attach","--sig-proxy=false", appName).redirectErrorStream(true);
            try(var buffer = new BufferedReader(new InputStreamReader(process.start().getInputStream()))){
                    String line;
                    while(!Thread.interrupted()&& (line = buffer.readLine() )!= null ){
                        if( line.startsWith("{")){
                            log.getBuffer().put(objMapper.readValue(line, LogModel.class));
                        }
                    }
                    return;
            }catch(IOException | InterruptedException e){
                return;
            }
        });
        thread.start();
        appsOutPut.put(appId, thread);
    }

    /**
     * observer methode, start detecting logs for the application app
     * @param app application to detect logs for
     * @param log log handler
     */
    @Override
    public void onCreateApp(Application app, Log log) {
        addOutPut(app.getFullDockerName(), app.getAppId(), log);
    }

    /**
     *  observer methode, stop detecting logs for a specific methode
     * @param appId method id
     */
    @Override
    public void onStop( int appId){
        appsOutPut.get(appId).interrupt();
    }

    /**
     * stop detecting all the applications logs before shutting down local kube
     */
    @Override
    public void onShutDown(){
        var sets = appsOutPut.entrySet();
        for(var set: sets){
            var thread = set.getValue();
            while(thread.isAlive()){
                thread.interrupt();
            }
        }
    }
}
