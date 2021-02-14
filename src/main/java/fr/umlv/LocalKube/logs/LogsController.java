package fr.umlv.LocalKube.logs;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * end point for log requests
 */
@RestController
@RequestMapping("/logs")
public class LogsController  {

    @Autowired
    @Qualifier("DataBase")
    private DataBase db;

    /**
     * test if the time stamp in the request is within the time range the user requested
     * @param timestamp time stamp in the log
     * @param time time stamp sent by the user
     * @return true if timestamp is less then time, else false
     */
    private static boolean dateLogCorrect(String timestamp, int time){
        try {
            Date actualDate = new Date();
            var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var diffInMillies = Math.abs(actualDate.getTime() - formatter.parse(timestamp.replace('T', ' ').replace('Z', ' ')).getTime());
            return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) < time;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date "+ timestamp + " is not correct "+e.getMessage());
        }
    }

    /**
     * get request for logs by time
     * @param time the time range requested
     * @return list of logs registered within the time requested
     */
    @GetMapping("/{time}")
    @ResponseBody
    public List<DataBaseRecord> filterTimeApp(@PathVariable("time") int time) {
        return db.filterLog(x -> dateLogCorrect(x.timestamp(), time));
    }

    /**
     * get request for logs by time and value
     * @param time time range requested
     * @param value value requested
     * @return list of logs registered within the time requested and matches the value
     */
    @GetMapping("/{time}/{value}")
    @ResponseBody
    public List<DataBaseRecord> filter(@PathVariable("time") int time, @PathVariable("value") String value) {
        return db.filterLog(x -> (x.appName().equals(value) || x.dockerInstance().equals(value))
                && dateLogCorrect(x.timestamp(), time));
    }

    /**
     * get request for logs by id
     * @param time time range requested
     * @param id id requested
     * @return list of logs registered within the time requested and matches the id
     */
    @GetMapping("/{time}/byId/{id}")
    @ResponseBody
    public List<DataBaseRecord> filterById(@PathVariable("time") int time, @PathVariable("id") int id) {
        return db.filterLog(x -> x.id() == id && dateLogCorrect(x.timestamp(), time));
    }

    /**
     * get request for the logs by time and app
     * @param time time range requested
     * @param app application requested
     * @return list of logs registered within the time requested for the application app
     */
    @GetMapping("/{time}/byApp/{app}")
    @ResponseBody
    public List<DataBaseRecord> filterByApp(@PathVariable("time") int time, @PathVariable("app") String app) {
        return db.filterLog(x -> x.appName().equals(app) && dateLogCorrect(x.timestamp(), time));
    }

    /**
     *get request for the logs by time and instance
     * @param time time requested by the user
     * @param instance instance requested
     * @returnlist list of logs registered within the time requested for the instance
     */
    @GetMapping("/{time}/byInstance/{instance}")
    @ResponseBody
    public List<DataBaseRecord> filterByInstance(@PathVariable("time") int time, @PathVariable("instance") String instance) {
        return db.filterLog(x -> x.dockerInstance().equals(instance) && dateLogCorrect(x.timestamp(), time));
    }


}
