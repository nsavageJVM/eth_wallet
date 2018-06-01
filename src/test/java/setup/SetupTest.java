package setup;


import learn.eth.EntryPoint;
import learn.eth.db.DbManager;
import learn.eth.service.WalletService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntryPoint.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class SetupTest {

    private static String user_home = System.getProperty("user.home");

    @Autowired
    private WalletService walletService;

    @Value("${wallet.test_menomic}")
    private String menomic;

    @Value("${wallet.testbase}")
    private String testbase;


    @Value("${wallet.test_db}")
    private String test_db;

    @Autowired
    DbManager dbManager;

    private Connection connection = null;


    @Before
    public void setUp() throws IOException, SQLException {

//        Files.deleteIfExists(Paths.get(testbase));
        Files.createDirectories(Paths.get(testbase));

        try {
            forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {

        }

        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/%s.db", user_home, test_db));

    }

//
    @After
    public void tearDown() throws IOException {

        FileUtils.deleteDirectory(Paths.get(testbase).toFile());
        FileUtils.forceDelete(Paths.get(String.format("%s/%s.db", user_home, test_db)).toFile());

    }


    @Test
    public void makeLocalWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException, CipherException, IOException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletUtils.generateWalletFile(menomic, ecKeyPair, new File(testbase), true);
    }


    @Test
    public void testInitDb() {
        dbManager.init(test_db);
    }


    @Test
    public void persistLocalWallet() throws SQLException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException, CipherException, IOException {

        testInitDb();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        String fileAsJson = WalletUtils.generateWalletFile(menomic, ecKeyPair, new File(testbase), true);
        dbManager.saveWalletLocation(menomic, fileAsJson);

        ScalarHandler<String> scalarHandler = new ScalarHandler<>();
        String query = "SELECT file_path FROM wally where menomic = '" + menomic + "';";
        QueryRunner runner = new QueryRunner();

        String file_path = runner.query(connection, query, scalarHandler);

        Assert.assertNotNull(file_path);
        System.out.println(file_path);

    }


    @Test
    public void loadCredentials() {

        testInitDb();
        walletService.createWallet(menomic);

        Credentials credentials =  walletService.loadCredentials(menomic);

        Assert.assertNotNull(credentials);

        System.out.println(credentials.getAddress());

    }


}
