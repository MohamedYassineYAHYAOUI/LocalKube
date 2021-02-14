package fr.umlv.LocalKube.logs;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * data Base record model
 */
public record DataBaseRecord(@JsonProperty("id") int id, @JsonProperty("app") String appName,  @JsonProperty("port") int port, @JsonProperty("service-port") int servicePort,
                             @JsonProperty("docker-instance") String dockerInstance,  @JsonProperty("message") String message, @JsonProperty("timestamp") String timestamp) {

}
