package fr.umlv.LocalKube.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * model for start request
 */
record ModelStart(@JsonProperty("app") String app, String imageName, int appPort) {

    /**
     * Json creator for the start model
     * @param app app name and info to serialize
     * @param imageName image name serialized
     * @param appPort app port serialized
     */
    @JsonCreator
    ModelStart{
        Objects.requireNonNull(app);
        if(!app.matches("[a-zA-Z]+[:][0-9]+")){
            throw new IllegalArgumentException("Application starter must follow template \"AppName:Port\"");
        }
        var tmp = app.split(":");
        imageName = tmp[0];
        appPort = Integer.parseInt(tmp[1]);
        if( appPort < 8080){
            throw new IllegalArgumentException("application "+imageName+" port must be higher then 8080");
        }

    }

}
