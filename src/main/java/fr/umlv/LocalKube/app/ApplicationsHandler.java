package fr.umlv.LocalKube.app;
import fr.umlv.LocalKube.logs.Log;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creation Process and Application handler Class
 */
class ApplicationsHandler {

    private final Loader appLoader;
    private final Launcher appLauncher;
    private final ApplicationFactory appFactory;
    private final Map<Integer, Application> appMap;
    private final Log logHandler ;
    private final HttpRequest requestsHandler;

    /**
     * Constructor for the application Handler
     * @param appFactory the application factory
     * @param appLoader the application loader
     * @param appLauncher the application launcher
     * @param reconstructedMap the map of all the running applications
     * @param logHandler the log handler
     * @param requestsHandler the request handler
     */
    ApplicationsHandler(ApplicationFactory appFactory, Loader appLoader, Launcher appLauncher, Map<Integer, Application> reconstructedMap, Log logHandler, HttpRequest requestsHandler){
        this.appFactory = Objects.requireNonNull(appFactory);
        this.appLoader = Objects.requireNonNull(appLoader);
        this.appLauncher =   Objects.requireNonNull(appLauncher);
        this.appMap  = Objects.requireNonNull(reconstructedMap);
        this.logHandler = Objects.requireNonNull(logHandler);
        this.requestsHandler = Objects.requireNonNull(requestsHandler);
    }

    /**
     * Process Methode to launch the container that contains the application
     * @param newApp application to launch
     * @throws IOException in case the launching the image failed
     * @throws InterruptedException in case loading the image to the docker failed
     */
    void launchApp(Application newApp) throws IOException, InterruptedException {
        Objects.requireNonNull(newApp);

        var appPath = "docker-images/"+newApp.getImageName()+".tar";
        if (!Files.exists(Path.of(appPath))) {
            throw new IllegalArgumentException("couldn't' find tar file for " + newApp.getImageName());
        }

        appLoader.loadImage(newApp.getImageName(), appPath);

        if(!appLoader.checkPortAvailable(newApp.getPort())){
            throw new IllegalArgumentException("Port "+newApp.getPort()+" is already used");
        };
        appLauncher.launchApp(newApp);
        appFactory.updateAppInstanceNumber(newApp.getImageName(), newApp.getInstanceNumber());
        appFactory.updateAppsId(newApp.getAppId());
        appMap.put(newApp.getAppId(), newApp);
        logHandler.observeNewApp(newApp);
    }

    /**
     * update the list of the containers currently running in docker, and send the new list
     * @return the list of the containers currently running
     * @throws IOException in case the update of the list failed
     */
        List<Application> appList() throws IOException {
        var newAppMap = appLauncher.recreateApplicationList(appFactory,  appMap.keySet(), logHandler);
        var itr = newAppMap.entrySet().iterator();
        while(itr.hasNext()){
            var appSet = itr.next();
            appFactory.updateAppsId(appSet.getKey());
            appMap.put(appSet.getKey(), appSet.getValue());

        }
        return appMap.values().stream().collect(Collectors.toList());

    }

    /**
     * stop an application by the Id appId, send a request to to Api-localKube to close the application
     * in the container and to shut down the container from the inside
     * @param appId id of the application to stop
     * @return new application
     */
    Application stopAppById(int appId) {
        var app = appMap.remove(appId);
        if(app == null){
            throw new IllegalArgumentException("no app with id "+appId);
        }
        logHandler.stopDetectingLogs(app.getAppId());
        try{
            requestsHandler.post("http://localhost:"+app.getServicePort()+"/shutdownContext");
        }catch (IllegalAccessException e){
            // error expected as the api shut down while request waiting for response
        }
        return app;
    }

    /**
     * prepare for localKube shutdown, disconnect from the api client in each container,
     * send interrupt signal to active threads
     * @throws IllegalAccessException in case the post request to the different api clients failed to disconnect from the
     * logs
     */
    void shutDown() throws IllegalAccessException {

        logHandler.shutDownApp();
        var set = appMap.entrySet();
        /*for(var app:set){
            requestsHandler.post("http://localhost:"+app.getValue().getServicePort()+"/disconnect");
        }*/

    }

    /**
     * create a new application using a ModelStart
     * @param appInfo mdelStart to create the application with
     * @return the new application
     */
    Application createNewApplication(ModelStart appInfo){
        return appFactory.createNewApplication(appInfo);
    }
}
