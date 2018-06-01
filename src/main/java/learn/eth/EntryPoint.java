package learn.eth;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
public class EntryPoint  {

	public static void main(String[] args) {  run(EntryPoint.class, args); }

	@Bean
	CommandLineRunner runner(){
		return args -> {
//
//			boolean result = Files.deleteIfExists(Paths.get("./spring-shell.log"));
//
//			System.out.println("created test  "+result);
		};
	}

}