package dev.unity.backend.gamebackend;

import org.springframework.boot.SpringApplication;

public class TestGamebackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(GamebackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
