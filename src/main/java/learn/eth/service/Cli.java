package learn.eth.service;


import learn.eth.config.PropertiesConfig;
import learn.eth.service.shell.ConsoleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;
import rx.Subscription;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.ansi.AnsiColor.*;


// https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/#_writing_your_own_commands

@ShellComponent
public class Cli {


    @Autowired
    private WalletService walletService;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    private Security auth;

    @Autowired
    ConsoleFormatter out;


    Availability logInAvailability() {

        return !auth.isLoggedIn() ? Availability.available() : Availability.unavailable("already logged in");
    }

    @ShellMethod("Log in with password")
    public String logIn(String password) {

        if (auth.login(password)) {
            walletService.createSession();
            return out.formatSuccess(Security.LOG_IN_SUCCESS);
        } else {
            return out.formatFail(Security.LOG_IN_FAIL);
        }
    }


    @ShellMethod("Create a Wallet requires a suitable menomic")
    public String createWallet(String menomic) {

        if (!auth.isLoggedIn()) {
            return out.formatFail(Security.NOT_AUTHORISED);
        }

        if (menomic.length() < 15) {
            return out.formatFail( "needs a few more words");
        }

        walletService.createWallet(menomic);

        return out.formatSuccess( menomic);
    }


    @ShellMethod("Show Local Wallet Balance")
    public void getLocalWalletBalance(String menomic) {
        if (!auth.isLoggedIn()) {
            out.consoleOutError(Security.NOT_AUTHORISED);
        }

        Subscription disposable =   walletService.getBalance(menomic).subscribe(
                balance -> {
                     out.consoleOutInfo(String.format("Local account balance: %s" ,balance.toString()));
                },
                e -> {
                    out.consoleOutError(e.getLocalizedMessage());
                },
                ()-> {
                    out.consoleOutSuccess( "request completed");
                });
        if(disposable.isUnsubscribed()) {

            disposable.unsubscribe();
        }

    }


    @ShellMethod("Show Remote Wallet Balance")
    public void getRemoteWalletBalance() {

        if (!auth.isLoggedIn()) {
             out.consoleOutError(Security.NOT_AUTHORISED);
        }

        Subscription disposable =  walletService.getRemoteBalance().subscribe(balance -> {
                    out.consoleOutInfo(String.format("Remote account balance: %s" ,balance.toString()));
                },
                e -> {
                    out.consoleOutError(e.getLocalizedMessage());
                },
                ()-> {
                    out.consoleOutSuccess( "request completed");
                });

        if(disposable.isUnsubscribed()) {

            disposable.unsubscribe();
        }

    }


    @ShellMethod("Shows avaiable Menomics")
    public Table showMenomics() {

        if (!auth.isLoggedIn()) {
            String[][] data = new String[1][1];
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            data[0][0] = Security.NOT_AUTHORISED;
            return tableBuilder.addFullBorder(BorderStyle.fancy_light_double_dash).build();
        }
        List<String> menomics = new LinkedList<>();

        walletService.loadMenomics().subscribe(s -> menomics.addAll(s));

        String[][] data = new String[menomics.size()][2];
        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);

        for (int i = 0; i < menomics.size(); i++) {
            data[i][0] = i + 1 + "";
            data[i][1] = menomics.get(i);
        }

        return tableBuilder.addFullBorder(BorderStyle.fancy_light_double_dash).build();
    }


    @ShellMethod("Show deBug")
    public String deBug() {
        AtomicReference<String> result = new AtomicReference<>("");
        walletService.loadMenomics().subscribe(
                s -> {
                    result.set(AnsiOutput.toString(RED, String.join(",", s), DEFAULT));
                }
        );

        return result.get();
    }


}
