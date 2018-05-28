package learn.eth.service;

import learn.eth.config.PropertiesConfig;
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
import java.util.List;

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


    @Autowired
    private PropertiesConfig propertiesConfig;




    public Observable<String> createWallet(String menomic) {
        Observable<String>  callBack = null;

        try {
                ECKeyPair ecKeyPair = Keys.createEcKeyPair();

                /**
                 * Do this as we are accessing a system resource "file system"
                 *
                 * and we dont want to block the application
                 *
                 */
                 callBack = Observable.unsafeCreate(subscriber -> {

                    String fileAsJson= null;
                    try {
                        fileAsJson = WalletUtils.generateWalletFile(menomic, ecKeyPair,  new File(propertiesConfig.getBase()), true);
                         dbManager.saveWalletLocation(menomic, fileAsJson );
                    } catch (CipherException | IOException e) {

                    }
                    subscriber.onNext(fileAsJson);
                  //  subscriber.onCompleted();

                });


            } catch (  InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
                //
            }
        return callBack;
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




    public Observable<List<String>> loadMenomics( ) {

        /**
         * Do this as we are accessing a system resource "database"
         *
         * and we dont want to block the application
         *
         */
        Observable<List<String>> callBack = Observable.just( dbManager.findAllMenomics( ));

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
