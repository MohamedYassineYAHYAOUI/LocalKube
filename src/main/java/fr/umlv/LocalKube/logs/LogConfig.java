package fr.umlv.LocalKube.logs;

import fr.umlv.LocalKube.app.Application;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Configuration
class LogConfig {

    @Autowired
    private OutPutObserver outPutObserver;

    @Autowired
    private BddObserver bddObserver;

    @Value("${log.dataBase.url}")
    private String dataBaseUrl;

    @Autowired
    @Qualifier("DataBase")
    private DataBase dataBase;

    @Autowired
    @Qualifier("CsvFile")
    private CsvFile csvFile;


    @Autowired
    @Qualifier("logBuffer")
    private SynchronizedBlockingBuffer buffer;


    /**
     * bean factory methode for the log handler
     * @return Log handler
     */
    @Primary
    @Bean(name="logHandlerBean")
    Log logHandlerBean(){
        var observers= new ArrayList<LogObserver>();
        observers.add(outPutObserver);
        observers.add(bddObserver);
        observers.add(dataBase);
        observers.add(csvFile);
        var log = new Log(observers, buffer);
        log.detectLogs();
        return log;
    }

    /**
     * bean factory methode for the database
     * @return the database
     */
    @Bean(name="bddThread")
    Thread dataBaseThread(){
        return new Thread(()->{
            while(!Thread.interrupted()){
                try{
                    var log= buffer.take();
                    dataBase.addDataBase(log);
                    if(!csvFile.correctTimeFileWriter())
                        csvFile = csvFile.createNewCsvFile();
                    csvFile.addToFile(log);
                }catch(InterruptedException e){
                    return;
                }
            }
            return;
        });
    }

    /**
     * bean factory methode for Jdbi
     * @return Jdbi object
     */
    @Bean
    Jdbi jdbiBean(){
        try {
            var connection = DriverManager.getConnection(dataBaseUrl);
            var jdbi = Jdbi.create(connection);
            jdbi.withHandle(handle -> handle
                    .execute("CREATE TABLE record ( id INTEGER," +
                            "message VARCHAR, timestamp TIMESTAMP)"
                    ));
            return jdbi;
        } catch (SQLException throwables) {
            throw new IllegalStateException("Data base already exists");
        }
    }

    /**
     * bean factory methode for creating map of id and application
     * @return map created
     */
    @Bean
    Map<Integer, Application> dataBaseMapBean(){
        return new HashMap<>();
    }
}
