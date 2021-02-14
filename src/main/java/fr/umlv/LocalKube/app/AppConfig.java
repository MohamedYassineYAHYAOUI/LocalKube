package fr.umlv.LocalKube.app;


import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.Jib;
import fr.umlv.LocalKube.logs.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

/***
 * Configuration Class and bean factory Methods
 * for the package app
 */
@Configuration
class AppConfig {

    @Value("${app.image.baseImage}")
    private String baseImage;

    @Value("${app.directory.jar}")
    private String jarDirectory;

    @Value("${app.directory.images}")
    private String imagesDirectory;

    @Value("${app.path.localKubeApi}")
    private String localKubeLibraryPath;

    @Autowired
    private Loader appLoader;

    @Autowired
    private Launcher appLauncher;

    @Autowired
    private ApplicationFactory appFactory;

    @Autowired
    @Qualifier("logHandlerBean")
    private Log logHandler;

    @Autowired
    private HttpRequest requestsHandler;

    /***
     * Bean Factory methode for the class ImageFactory
     * @return an instance of the object ImageFactory
     */
    @Bean
    ImageFactory imageFactoryBean(){
        try{
            return new ImageFactory(jarDirectory,imagesDirectory,Jib.from(baseImage), Path.of(localKubeLibraryPath));
        }catch(InvalidImageReferenceException e){
            throw new IllegalArgumentException("base image "+baseImage+" not valid");
        }
    }

    /***
     * Bean factory methode for the class ApplicationsHandler
     * @return an instance of the object ApplicationsHandler
     * @throws IOException in case methode failed te recreate the application list
     */
    @Bean
    ApplicationsHandler applicationsHandlerBean() throws IOException {
        var appMap = appLauncher.recreateApplicationList(appFactory, new HashSet<>(), logHandler);
        return new ApplicationsHandler(appFactory, appLoader, appLauncher, appMap, logHandler, requestsHandler);
    }



}
