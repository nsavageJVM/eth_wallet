package learn.eth;


import learn.eth.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication

public class EntryPoint  {

	private final Logger logger = LoggerFactory.getLogger("Command Line: ");


	@Autowired
	private static WalletService walletService;

	public EntryPoint(){

	}


	public static void main(String[] args) {

		// https://devops.datenkollektiv.de/migrating-from-spring-shell-12-to-20.html

		run(EntryPoint.class, args);


		}

}