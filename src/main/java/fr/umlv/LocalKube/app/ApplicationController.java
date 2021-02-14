package fr.umlv.LocalKube.app;

import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * SpringBoot end-point class for the resource app
 */
@RestController
@RequestMapping("/app")
public class ApplicationController {
    private final ApplicationsHandler appsHandler;

    private final ImageFactory docker;

    /**
     * Constructor for the ApplicationController, uses constructor injection
     * @param appsHandler the Application Handler for localKube
     * @param docker the image factory for localKube
     */
    public ApplicationController(ApplicationsHandler appsHandler, ImageFactory docker) {
        this.appsHandler = appsHandler;
        this.docker = docker;
    }

    /**
     * post request methode to launch the application, information to create the application provided in the request body
     * and must follow ModelStart format
     * @param appInfo the information format of the request body, serialized to ModelStart object
     * @return the new application launched in a container
     * @throws IOException in case the application files is missing
     * @throws InterruptedException in case the process was interrupted while creating the application
     */
    @PostMapping("/start")
    @ResponseBody
    public Application launchApp(@RequestBody ModelStart appInfo) throws IOException, InterruptedException {

        if(!Files.exists(Path.of("apps/"+appInfo.imageName()+".jar"))){
            throw new IllegalArgumentException("file apps/"+appInfo.imageName()+".jar missing");
        }
        Application newApp =appsHandler.createNewApplication(appInfo);
        docker.generateJar(newApp);
        appsHandler.launchApp(newApp);
        return newApp;
    }

    /**
     * get request methode to list all the containers currently active in docker
     * @return list of the active containers as applications
     * @throws IOException in case request failed to recreate the list
     */
    @GetMapping("/list")
    @ResponseBody
    public List<Application> listApps() throws IOException {
        return List.copyOf(appsHandler.appList());
    }

    /**
     * post request to stop a specific container by its id, request must follow ModelStop model
     * @param modelStop the container to stop
     * @return the application stopped
     */
    @PostMapping("/stop")
    public Application stopApp(@RequestBody ModelStop modelStop){
        return appsHandler.stopAppById(modelStop.id());
    }

    /**
     * Operation to execute before Local Kube closes
     * @throws IllegalAccessException in case the shut down operation failed
     */
    @PreDestroy
    void destroy() throws IllegalAccessException {
        appsHandler.shutDown();
    }
}
