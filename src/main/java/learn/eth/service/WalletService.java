package learn.eth.service;

import learn.eth.config.PropertiesConfig;
import learn.eth.db.DbManager;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
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
    private DbManager dbManager;


    @Autowired
    private PropertiesConfig propertiesConfig;



    public  String createWallet(String menomic) {
        String fileAsJson = null;

        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();


                try {
                    fileAsJson = WalletUtils.generateWalletFile(menomic, ecKeyPair,
                                    new File(propertiesConfig.getTestbase()), true);
                    dbManager.saveWalletLocation(menomic, fileAsJson);
                } catch (CipherException | IOException e) {

                }


        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            //
        }
        return fileAsJson;
    }


    public Credentials loadCredentials(String menomic) {

        Credentials credentials = dbManager.getUserCredentials(menomic);

        return credentials;
    }

}
