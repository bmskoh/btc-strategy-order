package com.gmail.bmskoh.strategyapp;

import com.gmail.bmskoh.strategyapp.services.IStrategyOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StrategyApplication implements CommandLineRunner {

	@Autowired
	IStrategyOrderService strategyOrderService;

	public static void main(String[] args) {
		SpringApplication.run(StrategyApplication.class, args);

	}

	/**
	 * As of 12/10/2019, start the app as a command line app.
	 */
	@Override
	public void run(String... args) {
		// Start the app by calling startService of service object.
		strategyOrderService.startService();
	}
}
