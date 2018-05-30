package learn.eth.service;


import learn.eth.config.PropertiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.ansi.AnsiColor.*;


// https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/#_writing_your_own_commands

@ShellComponent
public class Cli {


    @Autowired
    private  WalletService walletService;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    private Security auth;


    Availability logInAvailability() {

        return !auth.isLoggedIn() ? Availability.available() :Availability.unavailable("already logged in");
    }

    @ShellMethod("Log in with password")
    public String logIn( String password) {

       if(auth.login(password)) {
           return  AnsiOutput.toString(GREEN, Security.LOG_IN_SUCCESS, DEFAULT);
       } else {
           return  AnsiOutput.toString(BRIGHT_RED, Security.LOG_IN_FAIL, DEFAULT);
       }

    }



    @ShellMethod("Create a Wallet requires a suitable menomic")
    public String createWallet( String menomic) {

        if(!auth.isLoggedIn()) {
            return  AnsiOutput.toString(BRIGHT_RED, Security.NOT_AUTHORISED, DEFAULT);
        }

        if(menomic.length() <  40) {
            return  AnsiOutput.toString(RED, "needs a few more words", DEFAULT);
        }

        walletService.createWallet(menomic);

        return  AnsiOutput.toString(GREEN, menomic, DEFAULT);
    }


    @ShellMethod("Show Local Wallet Balance")
    public String getLocalWalletBalance( String menomic) {


        if(!auth.isLoggedIn()) {
            return  AnsiOutput.toString(BRIGHT_RED, Security.NOT_AUTHORISED, DEFAULT);
        }
        AtomicReference<String> result = new AtomicReference<>("");

         walletService.getBalance(menomic).subscribe(balance -> {
             result.set(AnsiOutput.toString(RED, balance.toString(), DEFAULT));
         },
         e -> {
             System.out.println(e.getLocalizedMessage());
         });


        return  result.get();
    }



    @ShellMethod("Show Remote Wallet Balance")
    public String getRemoteWalletBalance( )  {

        if(!auth.isLoggedIn()) {
            return  AnsiOutput.toString(BRIGHT_RED, Security.NOT_AUTHORISED, DEFAULT);
        }
        AtomicReference<String> result = new AtomicReference<>("");

        walletService.getRemoteBalance() .subscribe(balance -> {
                    result.set(AnsiOutput.toString(RED, balance.toString(), DEFAULT));
                },
                e -> {
                    System.out.println(e.getLocalizedMessage());
                });


        return  result.get();
    }



    @ShellMethod("Shows avaiable Menomics")
    public Table showMenomics( ) {

        if(!auth.isLoggedIn()) {
            String[][] data = new String[1][1];
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model) ;
            data[0][0] = Security.NOT_AUTHORISED;
            return tableBuilder.addFullBorder(BorderStyle.fancy_light_double_dash).build();
        }
        List<String> menomics = new LinkedList<>();

        walletService.loadMenomics().subscribe(s -> menomics.addAll(s));

        String[][] data = new String[2][menomics.size()];
        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model) ;

        for(int i = 0; i < menomics.size(); i++) {
                data[i][0] = i+1+"";
                data[i][1] = menomics.get(i);
        }

        return tableBuilder.addFullBorder(BorderStyle.fancy_light_double_dash).build();
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
