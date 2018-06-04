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

    @Before
    public void setUp() {
        dbManager.init("wally_1");
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));

//        localCredentials = walletService.loadCredentials(menomic);
//        localAccount = localCredentials.getAddress();


    }


    //  ./gradlew test --tests *LocalWalletServiceTest.runCAccountTest
    //  ./gradlew --rerun-tasks  test --tests *LocalWalletServiceTest.runCAccountTest   --info

    @Test
    public void runCAccountTest() throws IOException, ExecutionException, InterruptedException {

         walletService.createWallet("this is a test");

         localCredentials =  walletService.loadCredentials("this is a test");
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


}
