package fr.umlv.LocalKube.api.task;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


public class Observer {
    private final Logs logs;
    private final String imageName;
    private final int appId;

    private Observer(String imageName, int appId){
        this.imageName = Objects.requireNonNull(imageName) ;
        this.logs = new Logs();
        this.appId = appId;
    }

    /**
     * factory methode for log Observer
     * @param args application aguments
     * @return a new log observer
     */
    public static Observer createMonitor(String ...args) {
        Objects.requireNonNull(args);
        if(args.length != 2){
            throw new IllegalArgumentException("monitor arguments are invalid");
        }
        return new Observer(args[0], Integer.parseInt(args[1]));
    }

    /**
     * check if the application exited on code 0, if false send error log to localKube and close the Api
     * @param code exit code of the application
     */
    private void checkExitCode(int code){
        if(code!=0){
            var log = logs.formatLog(appId, imageName+" finished with exit code "+code);
            System.out.println(Logs.toJson(log));
            System.exit(0);
        }
    }


    /**
     * detect, format and send logs to localKube, logs detected will be filtered in localKube and registered to the data base
     */
    public void runApp() {

        Process tmp = null;
        try {
            tmp = new ProcessBuilder("java", "-jar", imageName + ".jar").redirectErrorStream(true).start();
            try (var buffer = new BufferedReader(new InputStreamReader(tmp.getInputStream()))) {
                String line;
                while (tmp.isAlive()) {
                    line = buffer.readLine();
                    if (line != null) {
                        var log = logs.formatLog(appId, line);
                        System.out.println(Logs.toJson(log));
                    }
                }
                checkExitCode(tmp.exitValue());
            }
        } catch (IOException e) {
            checkExitCode(tmp.exitValue());
        }
    }


}
