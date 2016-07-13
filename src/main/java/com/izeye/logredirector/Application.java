package com.izeye.logredirector;

import com.izeye.logredirector.core.service.LogRedirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by izeye on 16. 7. 12..
 */
@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {
	
	@Autowired
	private LogRedirector logRedirector;
	
	@Override
	public void run(String... args) throws Exception {
		this.logRedirector.run();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
