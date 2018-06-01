package learn.eth.service;

import learn.eth.config.PropertiesConfig;
import learn.eth.db.DbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@Service(value = "walletService")
public class WalletService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    @Autowired
    private DbManager dbManager;


    @Autowired
    private PropertiesConfig propertiesConfig;

    private Web3j web3j;

    private BigInteger acountBalance;


    @PostConstruct
    void init() {
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
    }



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


    public Credentials loadCredentials(String menomic) {

        Credentials credentials = dbManager.getUserCredentials(menomic);

        return credentials;
    }


    public Observable<List<String>> loadMenomics() {

        Observable<List<String>> callBack = Observable.just(dbManager.findAllMenomics());

        return callBack;
    }




    public String getRinkbySrcAccount() {

        return env.getProperty("rinkby.source.address");

    }


}
