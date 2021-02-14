package fr.umlv.LocalKube.app;

import com.google.cloud.tools.jib.api.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Application creation unit tests")

class ApplicationCreationUnitTest {



    @Nested
    @DisplayName("Application Factory Unit Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)

    class ApplicationFactoryUnitTest{

        private ApplicationFactory appFactory;
        private List<ModelStart> modelStartList;



        @BeforeEach
        void createAppFactory(){
            appFactory = new ApplicationFactory();
        }




        @Test
        @DisplayName("Creation should fail on wrong app wrong format")
        void shouldFailOnStartWithWrongFormat(){
            assertAll(
                    ()->{assertThrows(IllegalArgumentException.class, ()->{new ModelStart(":8080", "imageTest", 8080);});},
                    ()->{assertThrows(IllegalArgumentException.class, ()->{new ModelStart("imageTest:", "imageTest", 8080);});},
                    ()->{assertThrows(IllegalArgumentException.class, ()->{new ModelStart("imageTest:test", "imageTest", 8080);});},
                    ()->{assertThrows(IllegalArgumentException.class, ()->{new ModelStart("8080:imageTest", "imageTest", 8080);});}
            );
        }


        @Test
        @DisplayName("Creation should fail on null arguments")
        void shouldFailOnNullArguments(){
            assertAll(
                    ()->{assertThrows(NullPointerException.class, ()->{appFactory.createNewApplication(null);});},
                    ()->{ assertThrows(NullPointerException.class, ()->{appFactory.createNewApplication(new ModelStart(null, "imageTest", 8080));});}
                    );
        }


        @Test
        @DisplayName("Creation should fail on negative arguments")
        void shouldFailOnNegativeArguments(){
            assertThrows(IllegalArgumentException.class, ()->{appFactory.createNewApplication(new ModelStart("imageTest:-8080", "imageTest", 8080));});

        }

        @Test
        @DisplayName("new application is not null")
        void shouldCreateNewApp(){
            var newApp1 = appFactory.createNewApplication(1, 8080, "imageTest", 1, System.currentTimeMillis());
            var newApp2 = appFactory.createNewApplication(new ModelStart("imageTest:8080", "imageTest", 8080));
            assertNotNull(newApp1);
            assertNotNull(newApp2);
        }

        @Test
        @DisplayName("Create new application from different parameters")
        void shouldCreateApplicationFromParams(){
            var newApp = appFactory.createNewApplication(1, 8080, "imageTest", 1, System.currentTimeMillis());
            assertAll("app info",
                    ()->{assertEquals(1, newApp.getAppId());},
                    ()->{assertEquals("imageTest", newApp.getImageName());},
                    ()->{assertEquals(1, newApp.getInstanceNumber());},
                    ()->{assertEquals(8080, newApp.getPort());});
        }

        @Test
        @DisplayName("Create several applications with correct ID")
        void shouldCreateSeveralApplicationsWithCorrectID(){
            for(int i=1; i<100; i++){
                appFactory.createNewApplication(i, 8080, "imageTest", i, System.currentTimeMillis() );
            }
            var newApp =appFactory.createNewApplication(new ModelStart("imageTest:8080", "imageTest", 8080));
            assertEquals(100, newApp.getAppId());
        }

    }


    @Nested
    @DisplayName("Docker Image Factory Unit Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ImageFactoryUnitTest{

        private ImageFactory imageFactory;
        private final String jarDir;
        private final String imagesDirectory ;
        private final JibContainerBuilder jib;
        private final Path localKubeApi;

        ImageFactoryUnitTest() throws InvalidImageReferenceException {
            this.jarDir = "apps";
            this.imagesDirectory = "docker-images";
            this.jib = Jib.from("openjdk:15-alpine");
            this.localKubeApi = Path.of("lib/local-kube-api.jar");
        }


        @BeforeAll
        void createImageFactory(){
            this.imageFactory = new ImageFactory(jarDir, imagesDirectory, jib,localKubeApi);
        }

        @Test
        @DisplayName("Image creation should fail on null arguments ")
        void shouldFailOnNullArgument(){
            assertAll(
                    ()->{assertThrows(NullPointerException .class, ()->{new ImageFactory(null, imagesDirectory, jib,localKubeApi);});},
                    ()->{assertThrows(NullPointerException .class, ()->{new ImageFactory(jarDir, null, jib,localKubeApi);});},
                    ()->{assertThrows(NullPointerException .class, ()->{new ImageFactory(jarDir, imagesDirectory, null,localKubeApi);});},
                    ()->{assertThrows(NullPointerException .class, ()->{new ImageFactory(jarDir, imagesDirectory, jib,null);});}
            );
        }

        @Test
        @DisplayName("Image creation should fail on wrong jar Path")
        void shouldFailOnWrongArguments1(){
            var imageFactory = new ImageFactory("missingJar", imagesDirectory, jib,localKubeApi);
            var appFactory = new ApplicationFactory();
            var app =appFactory.createNewApplication(1, 8080, "timertickslong", 1, System.currentTimeMillis());
            assertThrows(IllegalStateException.class, ()->imageFactory.generateJar(app));
        }



         @Test
        @DisplayName("should fail if application jar is missing")
        void shouldThrowOnMissingApplicationJar(){
             var appFactory = new ApplicationFactory();
             var app =appFactory.createNewApplication(1, 8080, "missingImage", 1, System.currentTimeMillis());
             assertThrows(IllegalStateException .class, ()->{imageFactory.generateJar(app);});
        }
    }

}
