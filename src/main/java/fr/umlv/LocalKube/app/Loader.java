package fr.umlv.LocalKube.app;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


@Component
class Loader extends ContainerChecker{

    /**
     * load the tar file in tarFilePath to docker with the name appName
     * @param appName image name
     * @param tarFilePath tar file path
     * @throws IOException in case load failed to find image path
     * @throws InterruptedException in case image load was interrupted
     */
    void loadImage(String appName, String tarFilePath) throws IOException, InterruptedException {
        Objects.requireNonNull(tarFilePath);
        Objects.requireNonNull(appName);
        new ProcessBuilder("docker", "load", "-i", tarFilePath).start().waitFor();
    }

    /**
     * check if the port is available to be used
     * @param port port to check
     * @return true if the port is available, else false
     * @throws IOException
     */
    boolean checkPortAvailable(int port) throws IOException {

        var activePorts = listAllActivePorts(mapOfContainersNamesAndStatus().keySet());
        return activePorts.entrySet().stream().filter(e->e.getValue().contains(port))
                .collect(Collectors.toList()).isEmpty();
    }

    /**
     * list all the active port for a set of containers name
     * @param containersNames set of containers name
     * @return map that affect for each container name, a list of its active ports
     * @throws IOException
     */
    private HashMap<String, List<Integer>> listAllActivePorts(Set<String> containersNames) throws IOException{
        Objects.requireNonNull(containersNames);
        var ports = new HashMap<String, List<Integer>>();
        ProcessBuilder processPort;
        for(var name : containersNames){
            processPort = new ProcessBuilder( "docker", "inspect","--format","{{range $p, $conf := .NetworkSettings.Ports}}{{range $por := $conf}}{{$por.HostPort}};{{end}}{{end}}", name).redirectErrorStream(true);
            try(var buffer = new BufferedReader(new InputStreamReader(processPort.start().getInputStream()))){
                String line;
                while((line = buffer.readLine()) != null) {
                    if(line.isBlank()){
                        continue;
                    }
                    var listPorts = Arrays.stream(line.split(";")).map(Integer::parseInt).collect(Collectors.toList());
                    ports.put(name, listPorts);
                }
            }
        }
        return ports;
    }


}
