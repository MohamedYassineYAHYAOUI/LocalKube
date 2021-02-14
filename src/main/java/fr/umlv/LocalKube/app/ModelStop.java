package fr.umlv.LocalKube.app;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * model for stop request
 */
record ModelStop(@JsonProperty("id") int id) {}
