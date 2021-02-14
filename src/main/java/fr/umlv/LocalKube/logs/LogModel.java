package fr.umlv.LocalKube.logs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * log model of the message coming from api client
 */
record LogModel( @JsonProperty("id") int id, @JsonProperty("message") String message, @JsonProperty("timeStamp") String timeStamp){

    @JsonCreator
     LogModel{
        Objects.requireNonNull(message);
         Objects.requireNonNull(timeStamp);
    }

}