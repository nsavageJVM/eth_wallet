package learn.eth.service;

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
    private  WalletService walletService;

    @Autowired
    private Security auth;

    private static final String remoteCheckUrl = "check transaction status at "+System.lineSeparator()+"https://rinkeby.etherscan.io/txsPending";

    private static final String TRANSACTION_RESULT_TEMPLATE = "Transaction hash: %s"+System.lineSeparator()+"%s";


    @ShellMethod("run a transaction from remote account")
    public String runTransaction( String menomic, String transactionAmountInEther) {

        if(!auth.isLoggedIn()) {
            return  AnsiOutput.toString(BRIGHT_RED, Security.NOT_AUTHORISED, DEFAULT);
        }

        AtomicReference<String> result = new AtomicReference<>("");
        walletService.loadTransactionBaseData(menomic).subscribe(dto -> {
            dto.setAmountEther(transactionAmountInEther );
            RawTransaction transaction =  conversionService.convert(dto, RawTransaction.class);
            // send the transaction with the local rinkby client
            walletService.sendTransaction(transaction, dto).subscribe(

                    s->  {
                        result
                            .set(
                             String.format(
                             TRANSACTION_RESULT_TEMPLATE, AnsiOutput.toString(GREEN, s, DEFAULT),
                              AnsiOutput.toString(YELLOW, remoteCheckUrl, DEFAULT )));
                        } );
            });

        return  result.get();
    }

}
