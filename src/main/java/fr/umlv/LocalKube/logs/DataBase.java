package fr.umlv.LocalKube.logs;

import fr.umlv.LocalKube.app.Application;
import org.checkerframework.checker.units.qual.A;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
@Component("DataBase")
class DataBase implements LogObserver{

    private final Jdbi jdbi;

    private final Map<Integer, Application> appRegistered;

    @Autowired
    DataBase(Jdbi jdbi, Map<Integer, Application> appRegistered){
        this.jdbi = Objects.requireNonNull(jdbi);
        this.appRegistered = Objects.requireNonNull(appRegistered);
    }

    /**
     * observer methode, add app to application map in tha data base
     * @param app application to register in the data base
     * @param log Log, ignored in treatment
     */
    @Override
    public void onCreateApp(Application app, Log log){
        Objects.requireNonNull(app);
        appRegistered.put(app.getAppId(), app);
    }

    /**
     * adds a LogModel log to the data base
     * @param log log to add to the data base
     */
    void addDataBase(LogModel log){
        jdbi.useHandle(handle -> {
            handle.createUpdate("Insert into record(id, message, timestamp) values (:id,:message, :timestamp)")
                    .bind("id", log.id())
                    .bind("message", log.message())
                    .bind("timestamp", log.timeStamp())
                    .execute();
        });
    }

    /**
     * filter the database depending on the result of the predicate
     * @param predicate a predicate to filter the data base with
     * @return return a list of DataBaseRecords that return true for the predicate
     */
    public List<DataBaseRecord> filterLog(Predicate<? super DataBaseRecord> predicate){
        Handle handle = jdbi.open();
        return
                handle.createQuery("select * from record order by timestamp")
                        .map((rs, ctx) ->
                                new DataBaseRecord(rs.getInt("Id"),
                                        appRegistered.get(rs.getInt("Id")).getApp(),
                                        appRegistered.get(rs.getInt("Id")).getPort(),
                                        appRegistered.get(rs.getInt("Id")).getServicePort(),
                                        appRegistered.get(rs.getInt("Id")).getDockerInstance(),
                                        rs.getString("message"),
                                        rs.getString("timestamp")))
                        .stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
    }
}
