package fr.umlv.LocalKube.app;

import fr.umlv.LocalKube.logs.Log;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;




@Component
class Launcher extends ContainerChecker{

    void launchApp(Application newApp) throws IOException {
        Objects.requireNonNull(newApp);
         var process = new ProcessBuilder("docker", "run","-d", "--name", newApp.getFullDockerName(), "-p",newApp.getPort() + ":8080", "-p",newApp.getServicePort()+":"+newApp.getServicePort(), newApp.getImageName()).redirectErrorStream(true);
        try(var buffer = new BufferedReader(new InputStreamReader(process.start().getInputStream()))){
            String line;
            while((line = buffer.readLine() )!= null){
                var msg = line.split(":");

                if(msg.length >= 3 && msg[1].trim().equals("Error response from daemon")){
                    throw new IllegalArgumentException("error while running app " +msg[2]);
                }
            }
        }
    }


    private String parseTimeStamp(String stringTS) throws  IOException {
        Objects.requireNonNull(stringTS);
        String ts;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            var date = dateFormat.parse(stringTS);
            ts= Long.toString(date.getTime());

        }catch(ParseException e){
            throw new IOException("couldn't parse time "+e);
        }
        return ts ;
    }

    private void convertInfoToMap(HashMap<String, String> map, String appName, String msgToParse) throws IOException {
        Objects.requireNonNull(map);
        Objects.requireNonNull(appName);

        var msg = msgToParse.split(";");

        var appNameInfo = appName.split("_");

        if(msg.length >= 3){
            map.put("start-time", parseTimeStamp(msg[0]));
            map.put("image", msg[1]);
            map.put("port", msg[3]);
            map.put("app-version", appNameInfo[1].split("-")[1]);
            map.put("id", appNameInfo[0]);
            map.put("docker-instance",appName );
        }else {
            throw new IOException("Failed to retrieve app " + appName + " info");
        }
    }


    private HashMap<String, String> mapAppInfo(String appName) throws IOException {
        Objects.requireNonNull(appName);
        var process = new ProcessBuilder("docker", "inspect", "--format",
                "{{.State.StartedAt}};{{.Config.Image}};{{range $p, $conf := .NetworkSettings.Ports}}{{range $por := $conf}}{{$por.HostPort}};{{end}}{{end}}"
                , appName).redirectErrorStream(true);
        var mapInfo = new HashMap<String, String>();
        try(var buffer = new BufferedReader(new InputStreamReader(process.start().getInputStream()))){
            String line;
            while((line = buffer.readLine() )!= null){
                convertInfoToMap(mapInfo, appName, line);
            };
        }
        return mapInfo;
    }

    /**
     * recreate the application list to contain all the running containers
     * @param appFactory application factory to recreate the running containers as applications
     * @param idsSet id set of the present containers
     * @param logHandler log handler
     * @return
     * @throws IOException
     */
    HashMap<Integer, Application> recreateApplicationList(ApplicationFactory appFactory, Set<Integer> idsSet, Log logHandler) throws IOException {
        HashMap<Integer, Application> appMap = new HashMap<>();
        HashMap<String, String> newAppInfo;
        var mapNameStatus = mapOfContainersNamesAndStatus();
        for(var name : mapNameStatus.keySet()){
            if( mapNameStatus.get(name).equals("up")){
                newAppInfo = mapAppInfo(name);
                var id = Integer.parseInt(newAppInfo.get("id"));
                if(! idsSet.contains(id)){
                    var app = appFactory.createNewApplication(id,Integer.parseInt(newAppInfo.get("port")),
                            newAppInfo.get("image"), Integer.parseInt(newAppInfo.get("app-version")), Long.parseLong(newAppInfo.get("start-time")));
                    appMap.put(app.getAppId(), app);
                    logHandler.observeNewApp(app);
                    appFactory.updateAppInstanceNumber(app.getImageName(), app.getInstanceNumber());
                    appFactory.updateAppsId(app.getAppId());
                }
            }
        }
        return appMap;
    }


}
