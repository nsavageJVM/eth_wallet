package learn.eth.service.shell;


import learn.eth.service.Security;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import java.io.PrintStream;

import static org.springframework.boot.ansi.AnsiColor.*;

@Component
public class ConsoleFormatter {



    private final PrintStream out = System.out;

    private static final String remoteCheckUrl = "check transaction status at "+System.lineSeparator()+"https://rinkeby.etherscan.io/txsPending";


    public String formatSuccess(String data) {
      return  AnsiOutput.toString(BRIGHT_BLUE, data, DEFAULT);
    }

    public String formatFail(String data) {
        return  AnsiOutput.toString(BRIGHT_RED, data, DEFAULT);
    }



    public String formatResult(String data) {
        String tHash="Transaction hash := ";
        return  AnsiOutput.toString(BRIGHT_YELLOW, tHash+data, DEFAULT);
    }


    public void consoleOutInfo(String msg) {

        this.out.print((char)27+"[33m" );
        this.out.printf(msg);
        this.out.print((char)27+"[0m" );
        this.out.println();

    }

    public void consoleOutSuccess(String msg) {
        this.out.print((char)27+"[3m" );
        this.out.print((char)27+"[34m" );
        this.out.printf(msg);
        this.out.print((char)27+"[0m" );
        this.out.println();

    }

    public void consoleOutError(String msg) {
        this.out.print((char)27 +"[3m");
        this.out.print((char)27+"[31m" );
        this.out.printf(msg);
        this.out.print((char)27+"[0m" );
        this.out.println();

    }

    public void formatTransactionResult(String s) {

        this.out.print((char)27+"[34m" );
        this.out.printf("Transaction hash: ");

        this.out.print((char)27+"[31mm" );
        this.out.printf(s);
        this.out.print((char)27+"[0m" );
        this.out.println();

    }


    public void consoleOutTransactionInfo(String transactions_completed) {

        this.out.print((char)27 +"[3m");
        this.out.print((char)27+"[32m" );
        this.out.printf(transactions_completed);
        this.out.println();
        this.out.print((char)27+"[0m" );
        this.out.print((char)27+"[47m" );
        this.out.print((char)27+"[30m" );
        this.out.printf(remoteCheckUrl);
        this.out.print((char)27+"[0m" );
        this.out.println();
    }
}
