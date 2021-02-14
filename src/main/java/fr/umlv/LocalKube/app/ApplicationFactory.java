package fr.umlv.LocalKube.app;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Application factory class the localKube
 */
@Component
 class ApplicationFactory {

    private final HashMap<String, Integer> appInstanceNumbers = new HashMap<>();

    @Value("${app.id.startValue}")
    private int appId;

    @Value("${app.servicePort.startValue}")
    private int serviceStartPort;

    /**
     * updates the application appName instance number with value instanceNumber if the new value is
     * higher then the old value
     * @param appName application to update
     * @param instanceNumber new instance number for the application
     * @return the new application instance number
     */
    int updateAppInstanceNumber(String appName, int instanceNumber){
        Objects.requireNonNull(appName);
        var appInstanceNumber = appInstanceNumbers.getOrDefault(appName,0)+1;
        if( instanceNumber >=appInstanceNumber){
            appInstanceNumber = instanceNumber+1;
        }
        appInstanceNumbers.put(appName, appInstanceNumber);
        return appInstanceNumber;
    }

    /**
     * create a new application from appInfo
     * @param appInfo ModelStart to create application with
     * @return the new application
     */
    Application createNewApplication(ModelStart appInfo){
        Objects.requireNonNull(appInfo);

        var instanceNumber = appInstanceNumbers.putIfAbsent(appInfo.imageName(), 1);
        return new Application(appId, appInfo.appPort(), appInfo.imageName() , appId + serviceStartPort, instanceNumber!=null?instanceNumber:1 );
    }


    /**
     * create new application from specific values
     * @param id application id
     * @param port application port
     * @param imageName application image to generate
     * @param instanceNumber application instance number
     * @param createTime start time of the application
     * @return new application created
     */
    Application createNewApplication(int id, int port, String imageName, int instanceNumber, long createTime ){
        Objects.requireNonNull(imageName);
        if(id >appId) {
            appId = id+1;
        }
        return new Application(id, port, imageName, id+serviceStartPort, instanceNumber, createTime);
    }

    /**
     * update the id counter with newId if this values is higher then the old value
     * @param newId new value of the id
     */
    void updateAppsId(int newId){
        if( newId>=appId){
            appId =newId+1;
        }
    }


}
