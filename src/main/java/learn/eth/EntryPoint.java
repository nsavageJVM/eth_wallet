package learn.eth;


import learn.eth.db.DbManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
public class EntryPoint  {


	private static String user_home = System.getProperty("user.home");



	@Value("${wallet.db}")
	private String db;

	@Autowired
	private DbManager dbManager;

	public static void main(String[] args) {  run(EntryPoint.class, args); }


	/* bypassed with spring shell on start up but processed at end **/

	@Bean
	CommandLineRunner runner(){
		return args -> {

			boolean result = Files.deleteIfExists(Paths.get("./spring-shell.log"));

			System.out.println("delete log file result "+result);



		};
	}

}