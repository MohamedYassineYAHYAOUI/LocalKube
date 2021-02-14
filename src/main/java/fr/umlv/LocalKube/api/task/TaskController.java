package fr.umlv.LocalKube.api.task;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

/**
 * end point for the api-localKube, allows the local kube to shut down the api from the inside of
 * the container
 */
@RestController
public class TaskController implements ApplicationContextAware {


    private ApplicationContext context;

    /**
     * shut down application request received from local kube
     */
    @PostMapping("/shutdownContext")
    public void shutdownContext() {
        System.out.println("disconnect from logs");
        ((ConfigurableApplicationContext) context).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }


}
