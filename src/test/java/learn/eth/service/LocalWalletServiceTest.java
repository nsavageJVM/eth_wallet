package learn.eth.service;

import learn.eth.EntryPoint;
import learn.eth.db.DbManager;
import learn.eth.service.qrcode.PaperWalletGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.Credentials;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntryPoint.class, properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@TestPropertySource(
        locations = "classpath:application.properties")
public class LocalWalletServiceTest {

    private final Logger logger = LoggerFactory.getLogger("Wallet Service Test");


    @Value("${wallet.base}")
    private String walletBase;

    @Value("${wallet.db}")
    private String db;

    @Autowired
    WalletService walletService;

    @Autowired
    PaperWalletGenerator paperWalletGenerator;

    @Autowired
    DbManager dbManager;

    private String localAccount;


    private Credentials localCredentials;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {

        dbManager.init(db);

        localCredentials = walletService.loadCredentials("this is a test wallet");
        localAccount = localCredentials.getAddress();

    }


    @Test
    public void PaperWalletTest() {

        paperWalletGenerator.runJasperPaperWalletFlow(localCredentials);


    }





    }
