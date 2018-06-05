package learn.eth.service;

import learn.eth.EntryPoint;
import learn.eth.db.DbManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntryPoint.class )
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class LocalWalletServiceTest {

    private final Logger logger = LoggerFactory.getLogger(LocalWalletServiceTest.class);

    @Autowired
    WalletService walletService;

    @Autowired
    DbManager dbManager;

    private Web3j web3j;

    private String remoteAccount;

    private String localAccount;

    private BigDecimal amountEther = valueOf(0.0000123);

    private Credentials localCredentials;
    private BigInteger localAccountBalanceEther;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        dbManager.init("wally_1");
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));

        localCredentials = walletService.loadCredentials("this is a test");
        localAccount = localCredentials.getAddress();

        EthGetBalance localAccountBalance = web3j
                .ethGetBalance(localAccount, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        localAccountBalanceEther = localAccountBalance.getBalance();


    }


    //  ./gradlew test --tests *LocalWalletServiceTest.runCAccountTest
    //  ./gradlew --rerun-tasks  test --tests *LocalWalletServiceTest.runCAccountTest   --info

    @Test
    public void runCAccountTest() throws IOException, ExecutionException, InterruptedException {

         // walletService.createWallet("this is a test");
         // localCredentials =  walletService.loadCredentials("this is a test");
         logger.info("local Address: {}", localCredentials.getAddress());
         remoteAccount = walletService.getRinkbySrcAccount();
         logger.info("remoteAccount Address: {}", remoteAccount);

    }



    @Test
    public void runChainDiagnostics() throws IOException, ExecutionException, InterruptedException {


        logger.info("rinkkby local wallet address: {}", localAccount);
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gas_price = ethGasPrice.getGasPrice();
        logger.info("rinkkby gas_price: {}", gas_price);
        logger.info("rinkkby gas_price will not be enogh for remote chain need check metamask");


        EthBlockNumber etBlockNumber = web3j.ethBlockNumber().send();
        BigInteger blockNumber = etBlockNumber.getBlockNumber();
        logger.info("rinkkby blockNumber: {}", blockNumber);


        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(localAccount, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        logger.info("rinkkby local wallet transaction count or NONCE: {}", nonce);

    }



    @Test
    public void checkLocalBalanceTest() throws ExecutionException, InterruptedException {

        EthGetBalance localAccountBalance = web3j
                .ethGetBalance(localAccount, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        BigInteger localAccountBalanceEther = localAccountBalance.getBalance();
        logger.info("rinkkby local wallet getBalance: {}", localAccountBalanceEther);

    }


    @Test
    public void ensureFundsForRemoteTransfer() throws Exception {

        BigInteger amountWei = Convert.toWei(amountEther.toString(), Convert.Unit.ETHER).toBigInteger();
        logger.info("rinkkby transfer to wallet amount: {}", amountWei.toString());

        logger.info("rinkkby local wallet available amount: {}", localAccountBalanceEther.toString());

        BigInteger funds = localAccountBalanceEther.subtract(amountWei);
        logger.info("rinkkby local wallet available funds after transfer: {}", funds);
        assertTrue(funds.compareTo(BigInteger.ZERO) > 0);

    }

    @Test
    public void transferFromLocalToRemote() throws ExecutionException, InterruptedException {

        // set up transfer amount in wei
        BigInteger amountWei = Convert.toWei(amountEther.toString(), Convert.Unit.ETHER).toBigInteger();
        //  get the nonce
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(localAccount, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        // create a raw transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, Convert.toWei("2", Convert.Unit.GWEI).toBigInteger(), BigInteger.valueOf(21000), remoteAccount, amountWei);

        // sign the transaction with the local wallets credentials
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, localCredentials);
        String hexValue = Numeric.toHexString(signedMessage);

        // send the transaction with the local rinkby client
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        if (ethSendTransaction.hasError()) {
            logger.info("oops: {}", ethSendTransaction.getError().getMessage());
        } else if (ethSendTransaction.getResult() != null || ethSendTransaction.getTransactionHash() != null) {
            String transactionhash = ethSendTransaction.getTransactionHash();
            logger.info("TransactionHash: {}", transactionhash);
            assertNotNull(transactionhash);
            // https://rinkeby.etherscan.io/txsPending

        }


    }


}
