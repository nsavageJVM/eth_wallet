package learn.eth.service;

import learn.eth.db.DbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import rx.Observable;
import rx.Subscriber;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service(value="walletService")
public class WalletService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    @Autowired
    private Subscriber<String> shutDownHook;

    @Autowired
    private Subscriber<Credentials> credentialsHook;


    @Autowired
    private DbManager dbManager;



    public void createWallet(String menomic) {


            try {
                ECKeyPair ecKeyPair = Keys.createEcKeyPair();

                /**
                 * Do this as we are accessing a system resource "file system"
                 *
                 * and we dont want to block the application
                 *
                 */
                Observable callBack = Observable.unsafeCreate(subscriber -> {

                    String fileAsJson= null;
                    try {
                        fileAsJson = WalletUtils.generateWalletFile(menomic, ecKeyPair,  new File("/home/ubu/wally"), true);
                         dbManager.saveWalletLocation(menomic, fileAsJson );
                    } catch (CipherException | IOException e) {

                    }
                    subscriber.onNext(fileAsJson);
                    subscriber.onCompleted();

                });

                callBack.subscribe(shutDownHook);

            } catch (  InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
                //
            }
    }



    public Observable<Credentials> loadCredentials(String menomic ) {

        /**
         * Do this as we are accessing a system resource "database"
         *
         * and we dont want to block the application
         *
         */
        Observable<Credentials> callBack = Observable.just( dbManager.getUserCredentials(menomic));

        return callBack;
    }


    public String getRinkbySrcAccount() {

     return   env.getProperty("rinkby.source.address");


    }




    public void loadCredentialsCheckAndStop(String menomic ) {

        /**
         * Do this as we are accessing a system resource "database"
         *
         * and we dont want to block the application
         *
         */
        Observable callBack = Observable.unsafeCreate(subscriber -> {

            Credentials credentials  = dbManager.getUserCredentials(menomic);
            subscriber.onNext(credentials);
            subscriber.onCompleted();
        });

        callBack.subscribe(credentialsHook);

    }
}
