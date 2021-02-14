package fr.umlv.LocalKube.app;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import org.springframework.beans.factory.annotation.Autowired;



import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;

class ImageFactory {
    private final JibContainerBuilder baseImage;
    private final String jarDir;
    private final String imageDir;
    private final Path libApiPath;

    @Autowired
    ImageFactory(String jarPathDir, String imagePathDir ,JibContainerBuilder baseImage, Path libApiPath){
        this.jarDir = Objects.requireNonNull(jarPathDir);
        this.imageDir = Objects.requireNonNull(imagePathDir);
        this.baseImage = Objects.requireNonNull(baseImage);
        this.libApiPath = Objects.requireNonNull(libApiPath);
    }

    /**
     * create the docker image form  imageBuilder with the name imageName
     * @param imageBuilder imageBuilder to create image
     * @param imageName image name
     */
    private void containerizeImage(JibContainerBuilder imageBuilder, String imageName){
        Objects.requireNonNull(imageBuilder);
        try{
            imageBuilder.containerize(Containerizer.to(
                    TarImage.at(Path.of(imageDir+"/"+imageName+".tar"))
                            .named(imageName.toLowerCase())));
        } catch (IOException | InvalidImageReferenceException e) {
            throw new IllegalArgumentException("couldn't create tar file "+imageName+".tar in directory "+imageDir);
        } catch (CacheDirectoryCreationException e){
            throw new IllegalStateException("couldn't find directory "+e.getMessage());
        }catch (ExecutionException | InterruptedException e){
            throw new IllegalStateException("execution interrupted while creating tar file "+e.getMessage());
        } catch (RegistryException e){
            throw new IllegalStateException("Error occurred while interacting with a registry "+e.getMessage());
        }
    }

    /**
     * create the image tar file using the application app and the api-localKube jar file
     * @param app app to containerize
     */
    void generateJar(Application app) {
        Objects.requireNonNull(app);
        var imgName = app.getImageName();
        var jarPathList = Arrays.asList(Path.of(jarDir+"/"+imgName+".jar"), libApiPath);

        try{
            var imageBuilder = baseImage.addLayer(jarPathList,AbsoluteUnixPath.get("/") )
                    .setEntrypoint("java","-Dserver.port="+app.getServicePort(), "-jar", "local-kube-api.jar" )
                    .setProgramArguments(imgName,Integer.toString(app.getAppId()));

            containerizeImage(imageBuilder, imgName);

        }catch(IOException e){
            throw new IllegalArgumentException("couldn't find jar file "+imgName+".jar in directory "+jarDir);
        }
    }



}
