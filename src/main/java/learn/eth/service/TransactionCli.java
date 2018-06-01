package learn.eth.service;

import learn.eth.service.shell.ConsoleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.convert.ConversionService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.web3j.crypto.RawTransaction;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.ansi.AnsiColor.*;
import static org.springframework.boot.ansi.AnsiColor.DEFAULT;

@ShellComponent
public class TransactionCli {

    @Autowired
    @Qualifier("transaction")
    ConversionService conversionService;

    @Autowired
    ConsoleFormatter out;

    @Autowired
    private  WalletService walletService;

    @Autowired
    private Security auth;




    @ShellMethod("run a transaction from remote account (menomic, amount in ether)")
    public void runTransaction( String menomic, String transactionAmountInEther) {

        if(!auth.isLoggedIn()) {
            out.consoleOutError(Security.NOT_AUTHORISED);

        }
        walletService.loadTransactionBaseData(menomic).subscribe(dto -> {
            dto.setAmountEther(transactionAmountInEther );
            RawTransaction transaction =  conversionService.convert(dto, RawTransaction.class);
            // send the transaction with the local rinkby client
            walletService.sendTransaction(transaction, dto).subscribe(

                    s->  {
                         out.formatTransactionResult(s);
                        },
                    e-> {
                        out.consoleOutError(e.getLocalizedMessage());
                    },

                    ()-> {
                        out.consoleOutTransactionInfo( "transactions completed");
                    });
            });


    }

}
