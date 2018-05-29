package learn.eth.service;


import learn.eth.config.PropertiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.web3j.utils.Convert;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.ansi.AnsiColor.DEFAULT;
import static org.springframework.boot.ansi.AnsiColor.GREEN;
import static org.springframework.boot.ansi.AnsiColor.RED;


// https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/#_writing_your_own_commands

@ShellComponent
public class Cli {


    @Autowired
    private  WalletService walletService;

    @Autowired
    private PropertiesConfig propertiesConfig;



    @ShellMethod("Create a Wallet requires a suitable menomic")
    public String createWallet( String menomic) {

        if(menomic.length() <  40) {
            return  AnsiOutput.toString(RED, "needs a few more words", DEFAULT);
        }

        walletService.createWallet(menomic);

        return  AnsiOutput.toString(GREEN, menomic, DEFAULT);
    }


    @ShellMethod("Show Wallet Balance")
    public String getWalletBalance( String menomic) {
        AtomicReference<String> result = new AtomicReference<>("");

         walletService.getBalance(menomic).subscribe(balance -> {
             result.set(AnsiOutput.toString(RED, balance.toString(), DEFAULT));
         },
         e -> {
             System.out.println(e.getLocalizedMessage());
         });


        return  result.get();
    }


    @ShellMethod("Shows avaiable Menomics")
    public String showMenomics( ) {
        AtomicReference<String> result = new AtomicReference<>("");
        walletService.loadMenomics().subscribe(
                s -> {
                    result.set(AnsiOutput.toString(RED, String.join(",",  s), DEFAULT)); }
        );

        return  result.get();
    }



    @ShellMethod("Show deBug")
    public String deBug(  ) {
        AtomicReference<String> result = new AtomicReference<>("");
        walletService.loadMenomics().subscribe(
                s -> {
                    result.set(AnsiOutput.toString(RED, String.join(",",  s), DEFAULT)); }
                );

        return  result.get();
    }



}
