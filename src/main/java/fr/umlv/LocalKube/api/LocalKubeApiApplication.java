package fr.umlv.LocalKube.api;


import fr.umlv.LocalKube.api.task.Observer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * entry point for the API-localKube
 */
@SpringBootApplication
public class LocalKubeApiApplication {
	public static void main(String[] args) {

		SpringApplication.run(LocalKubeApiApplication.class, args);

		var monitor = Observer.createMonitor(args);
		monitor.runApp();

	}

}
