package fr.umlv.LocalKube.app;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Objects;


/***
 *Model for local kube application
 */
public class Application{


    private final AppTimer applicationTimer ;
    private final int id;
    private final int instanceNumber;
    private final int servicePort;
    private final String imageName;
    private final int port;

    /**
     * constructor for the application
     * @param id application id
     * @param port application port
     * @param imageName application image to generate
     * @param servicePort application service port
     * @param instanceNumber application instace number
     * @param startTime application start time
     */
    Application(int id, int port, String imageName, int servicePort, int instanceNumber, long startTime){
        this.imageName = Objects.requireNonNull(imageName);
        this.applicationTimer = new AppTimer(startTime);
        this.id = id;
        this.servicePort = servicePort;
        this.instanceNumber = instanceNumber;
        this.port = port;
    }

    /**
     * constructor for the application
     * @param id application id
     * @param port application port
     * @param imageName application image to generate
     * @param servicePort application service port
     * @param instanceNumber application instance number
     */
    Application(int id, int port, String imageName, int servicePort, int instanceNumber){
        this(id, port, imageName,  servicePort, instanceNumber, System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Application app && app.id == id
                && app.imageName.equals(imageName) && app.servicePort == servicePort
                && app.instanceNumber == instanceNumber && app.port == port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + instanceNumber;
        result = prime * result + servicePort;
        result = prime * result + port;
        return result;
    }

    /**
     * getter for name of the container
     * @return the name of the container
     */
    public String getFullDockerName(){return id+"_"+imageName +"-"+ instanceNumber;}

    /**
     * getter for image name
     * @return image name
     */
    String getImageName(){
        return imageName;
    }

    /**
     * getter for application instance number
     * @return application instance number
     */
    int getInstanceNumber(){
        return instanceNumber;
    }

    /**
     * getter for the application id
     * @return application id
     */
    @JsonProperty("id")
    public int getAppId() {
        return id;
    }

    /**
     * getter for the application and the port sent by the user
     * @return application name and port
     */
    @JsonProperty("app")
    public String getApp(){ return imageName+":"+port; }

    /**
     * getter for the application port
     * @return application port
     */
    @JsonProperty("port")
    public int getPort(){ return port;}

    /**
     * getter for the application service port
     * @return service port
     */
    @JsonProperty("service-port")
    public int getServicePort(){return servicePort;}

    /**
     * getter for the application docker instance
     * @return docker instance
     */
    @JsonProperty("docker-instance")
    public String getDockerInstance(){
        return imageName +"-"+ instanceNumber;
    }

    /**
     * getter for the application running time
     * @return the elapsed time
     */
    @JsonProperty("elapsed-time")
    public String getElapsedTime(){
        return applicationTimer.elapsedTime();
    }
}
