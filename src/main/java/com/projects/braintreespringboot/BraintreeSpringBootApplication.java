package com.projects.braintreespringboot;

import com.braintreegateway.BraintreeGateway;
import com.projects.braintreespringboot.configuration.BraintreeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.util.Arrays;

@SpringBootApplication
public class BraintreeSpringBootApplication {

	public static String DEFAULT_CONFIG_FILENAME = "config.properties";
	public static BraintreeGateway gateway;

	public static void main(String[] args) {

		File configFile = new File(DEFAULT_CONFIG_FILENAME);
		try {
			if(configFile.exists() && !configFile.isDirectory()) {
				gateway = BraintreeConfiguration.fromConfigFile(configFile);
			} else {
				gateway = BraintreeConfiguration.fromConfigMapping(System.getenv());
			}
		} catch (NullPointerException e) {
			System.err.println("Could not load Braintree configuration from config file or system environment.");
			System.exit(1);
		}
		SpringApplication.run(BraintreeSpringBootApplication.class, args);

	}
}
