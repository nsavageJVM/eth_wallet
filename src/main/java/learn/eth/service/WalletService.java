package learn.eth.service;

import learn.eth.config.PropertiesConfig;
import learn.eth.db.DbManager;
import learn.eth.service.transaction.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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


    public Observable<BigInteger> getBalance(String menomic) {
        return Observable.fromCallable(() -> loadBalance(menomic));
    }

    public Observable<BigInteger> getRemoteBalance() {
        return Observable.fromCallable(() -> loadRemoteBalance());
    }

    private BigInteger loadBalance(String menomic) {
        Credentials credentials = dbManager.getUserCredentials(menomic);

        String remoteAccount = credentials.getAddress();
        EthGetBalance remoteAccountBalance = null;
        try {
            remoteAccountBalance = web3j
                    .ethGetBalance(remoteAccount, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        acountBalance = remoteAccountBalance.getBalance();

        return acountBalance;
    }

    private BigInteger loadRemoteBalance() {

        String remoteAccount = getRinkbySrcAccount();
        BigInteger remoteAccountBalanceEther = BigInteger.ZERO;

        try {
            EthGetBalance remoteAccountBalance = web3j
                    .ethGetBalance(remoteAccount, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            remoteAccountBalanceEther = remoteAccountBalance.getBalance();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        return remoteAccountBalanceEther;
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


    public Observable<String> sendTransaction(RawTransaction transaction, TransactionDto dto) {

        Observable<String> callBack = null;

        // sign the transaction with the local wallets credentials
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, dto.getLocalCredentials());
        String hexValue = Numeric.toHexString(signedMessage);
        try {
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                callBack = Observable.just(ethSendTransaction.getError().getMessage());
                logger.info("oops: {}", ethSendTransaction.getError().getMessage());
            } else if (ethSendTransaction.getResult() != null || ethSendTransaction.getTransactionHash() != null) {
                String transactionhash = ethSendTransaction.getTransactionHash();

                callBack = Observable.just(transactionhash);

                logger.info("TransactionHash: {}", transactionhash);

                // https://rinkeby.etherscan.io/txsPending

            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return callBack;
    }


    public Observable<TransactionDto> loadTransactionBaseData(String menomic) {
        TransactionDto dto = new TransactionDto();
        dto.setRemoteAccount(getRinkbySrcAccount());
        Credentials credentials = dbManager.getUserCredentials(menomic);
        dto.setLocalCredentials(credentials);
        try {

            EthGetTransactionCount ethGetTransactionCount =
                    web3j.ethGetTransactionCount(dto.getLocalAccount(), DefaultBlockParameterName.LATEST).sendAsync().get();
            dto.setNonce(ethGetTransactionCount.getTransactionCount());
            ;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Observable<TransactionDto> callBack = Observable.just(dto);

        return callBack;
    }


    public String getRinkbySrcAccount() {

        return env.getProperty("rinkby.source.address");

    }


}
