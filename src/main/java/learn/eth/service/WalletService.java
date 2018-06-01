package learn.eth.service;

import learn.eth.config.PropertiesConfig;
import learn.eth.db.DbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service(value = "walletService")
public class WalletService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    @Autowired
    private DbManager dbManager;


    @Autowired
    private PropertiesConfig propertiesConfig;



    public Observable<String> createWallet(String menomic) {
        Observable<String> callBack = null;

        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();

            callBack = Observable.unsafeCreate(subscriber -> {

                String fileAsJson = null;
                try {
                    fileAsJson = WalletUtils.generateWalletFile(menomic, ecKeyPair, new File(propertiesConfig.getBase()), true);
                    dbManager.saveWalletLocation(menomic, fileAsJson);
                } catch (CipherException | IOException e) {

                }
                subscriber.onNext(fileAsJson);
                //  subscriber.onCompleted();

            });


        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            //
        }
        return callBack;
    }



}
