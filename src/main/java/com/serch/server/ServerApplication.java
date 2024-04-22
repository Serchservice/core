package com.serch.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the <a href="https://www.serchservice.com">Serch (a requestSharing and provideSharing platform)</a> server
 * that runs all its platforms.
 * This contains exclusively, all the codes that Serch depends on.
 *
 * @author <a href="https://iamevaristus.github.com">Evaristus Adimonyemma</a>
 */
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
}