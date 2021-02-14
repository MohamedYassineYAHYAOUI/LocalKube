package fr.umlv.LocalKube.app;

import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.Jib;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("images docker launcher unit tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class ApplicationLauncherUnitTest {

    private Application application;
    private final ApplicationsHandler appHandler;

    ApplicationLauncherUnitTest(ApplicationsHandler appHandler) {
        this.appHandler = appHandler;
    }


    @BeforeAll
    void createAppFactory() throws InvalidImageReferenceException {
        var appFactory = new ApplicationFactory();
        var imageFactory = new ImageFactory("apps", "docker-images",  Jib.from("openjdk:15-alpine"), Path.of("lib/local-kube-api.jar"));
        application =appFactory.createNewApplication(1, 8080, "timertickslong", 1, System.currentTimeMillis());
    }





}
