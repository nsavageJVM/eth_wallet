package learn.eth.service;

import learn.eth.EntryPoint;

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
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;


import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntryPoint.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class WalletServiceTest {

    private final Logger logger = LoggerFactory.getLogger("Wallet Service Test");

    @Value("${walet.local.menomic}")
    String menomic;

    @Autowired
    WalletService walletService;

    private Web3j web3j;

    private String srcAccount;

    @Before
    public void setUp() {
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));

    }

    @Test
    public void  runtransfer() throws IOException, ExecutionException, InterruptedException {

        srcAccount = walletService.getRinkbySrcAccount();
        logger.info("rinkkby source wallet address: {}", srcAccount);
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        System.out.println(ethGasPrice.getGasPrice().toString());

        EthBlockNumber etBlockNumber  = web3j.ethBlockNumber().send();
        System.out.println(etBlockNumber.getBlockNumber().toString());
        // walletService.loadCredentialsCheckAndStop( menomic) ;
         // https://github.com/web3j/web3j/issues/318

        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(srcAccount, DefaultBlockParameterName.LATEST).sendAsync().get();


        logger.info("rinkkby source wallet getTransactionCount or NONCE: {}", ethGetTransactionCount.getTransactionCount());

        EthGetBalance balance = web3j
                .ethGetBalance(srcAccount, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        logger.info("rinkkby source wallet getBalance: {}", balance.getBalance());


    }

}
