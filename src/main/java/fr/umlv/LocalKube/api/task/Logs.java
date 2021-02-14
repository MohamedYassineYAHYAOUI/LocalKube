package fr.umlv.LocalKube.api.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Objects;

class Logs {

    class Log{
        private final int id;
        private final String message;
        private final String timeStamp;

        /**
         * json creator for logs
         * @param id id of the application
         * @param message message log
         * @param timeStamp timeStamp of the log
         */
        @JsonCreator
        Log(@JsonProperty("id")int id, @JsonProperty("message") String message, @JsonProperty("timeStamp") String timeStamp){
            this.message = Objects.requireNonNull(message);
            this.timeStamp = Objects.requireNonNull(timeStamp);
            this.id = id;
        }
        int getId() {
            return id;
        }

        String getGetTimeStamp() {
            return timeStamp;
        }
        String getMessage() {
            return message;
        }
    }

    /**
     * json formatter for the log
     * @param log log to format
     * @return log formatted as json
     */
        static String toJson(Log log){
        return """
                {"id":"%d","message": "%s","timeStamp":"%s"}
                """.formatted(log.id, log.getMessage(), log.getGetTimeStamp());
        }

    /**
     * format appid and message as log
     * @param appId app id
     * @param message message to log
     * @return Log object
     */
        Log formatLog(int appId, String message){
            Objects.requireNonNull(message);
            var timeStamp = new Timestamp(System.currentTimeMillis());
            return new Log(appId, message, timeStamp.toInstant().toString());
        }
}
