package learn.eth;


import learn.eth.db.DbManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
public class EntryPoint  {


	private static String user_home = System.getProperty("user.home");

	@Value("${wallet.base}")
	private String walletBase;

	@Value("${wallet.db}")
	private String db;

	@Autowired
	private DbManager dbManager;

	public static void main(String[] args) {  run(EntryPoint.class, args); }


	@Bean
	CommandLineRunner runner(){
		return args -> {

			if(!Files.exists(Paths.get(walletBase))) {
				Files.createDirectories(Paths.get(walletBase));
			}

			if(!Files.exists(Paths.get(String.format("%s/%s.db",user_home, db)))) {
				dbManager.init(db);
			}
		};
	}

}