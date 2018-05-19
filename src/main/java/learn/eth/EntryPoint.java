package learn.eth;


import learn.eth.service.WalletService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class EntryPoint  {


	private static WalletService walletService;


	public static void main(String[] args) {

		ApplicationContext appContext =
		SpringApplication.run(EntryPoint.class, args);
		walletService = appContext.getBean( WalletService.class);
		// walletService.createWallet(args[0]);
		walletService.loadCredentialsCheckAndStop(args[0]) ;
		}

	}


