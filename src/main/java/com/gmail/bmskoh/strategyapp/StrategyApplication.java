package com.gmail.bmskoh.strategyapp;

import javax.websocket.ContainerProvider;

import com.gmail.bmskoh.strategyapp.comm.ConnectionClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StrategyApplication implements CommandLineRunner {

	@Autowired
	ConnectionClient commClient;

	public static void main(String[] args) {
		SpringApplication.run(StrategyApplication.class, args);

	}

	/**
	 * As of 12/10/2019, start the app as a command line app.
	 */
	@Override
	public void run(String... args) {
		// Start the app by starting websocket connection to BTCMarkets websocket
		// server.
		commClient.startConnection(ContainerProvider.getWebSocketContainer());
	}
}
