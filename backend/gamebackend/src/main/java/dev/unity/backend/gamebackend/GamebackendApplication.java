package dev.unity.backend.gamebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GamebackendApplication {

	public static void main(String[] args) {
		System.out.println("JVM timezone: " + java.util.TimeZone.getDefault().getID());
		SpringApplication.run(GamebackendApplication.class, args);
	}

}
