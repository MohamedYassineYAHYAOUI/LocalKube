package fr.umlv.LocalKube.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * abstract class for the class Launcher and Loader
 */
abstract class ContainerChecker {
    /**
     * methode to get running containers names
     * @return map of the container and their status (down or up)
     * @throws IOException in case of error while reading process output
     */
    HashMap<String, String> mapOfContainersNamesAndStatus() throws IOException {
        String[] commands = {"docker", "ps", "-a", "--format", "{{.Names}};{{.Status}}"};
        var processPort = new ProcessBuilder(commands).redirectErrorStream(true);
        var nameAndStatus =new HashMap<String, String>() ;

        try(var buffer = new BufferedReader(new InputStreamReader(processPort.start().getInputStream()))){
            String line;
            while((line = buffer.readLine()) != null){
                line.replace("\"","");
                var name = line.split(";");
                var statusTab = name[1].toLowerCase().split(" ");
                nameAndStatus.put(name[0], statusTab[0]);
            }
        }
        return nameAndStatus;
    }
}
