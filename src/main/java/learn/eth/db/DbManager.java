package learn.eth.db;

import learn.eth.config.PropertiesConfig;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;


/**
 * Persist etherium wallet artifacts with sql lite
 */
@Component
public class DbManager {


    @Autowired
    private PropertiesConfig propertiesConfig;

    private Connection connection = null;
    private static Boolean isInitial = false;

     private static String user_home = System.getProperty("user.home");


    static {
        try {
            forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @PostConstruct
    void init() {

        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/wally.db", user_home));
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);

            if (isInitial) {
                statement.executeUpdate("drop table if exists wally");
                statement.executeUpdate("create table wally (menomic string,  file_path string)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveWalletLocation(String menomic, String file_path) {

        QueryRunner run = new QueryRunner();
        try {
            run.update(connection, "insert into wally values (?,?)",  new Object[]{menomic, file_path});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Credentials getUserCredentials(String menomic) {
        Credentials  credentials = null;
        QueryRunner runner = new QueryRunner();
        ScalarHandler<String> scalarHandler = new ScalarHandler<>();
        String query ="SELECT file_path FROM wally where menomic = '" + menomic + "';";
        try {
        String file_path = runner.query(connection, query, scalarHandler);
        credentials = WalletUtils.loadCredentials(menomic, String.format(propertiesConfig.getTemplate(), file_path ) );

        } catch (IOException | SQLException | CipherException e) {
            e.printStackTrace();
        }
        return credentials;
    }


    public  List<String>  findAllMenomics() {

        List<String> results = new ArrayList<String>();
        QueryRunner runner = new QueryRunner();

        try {
            results = runner.query(connection,"SELECT menomic FROM wally", new ColumnListHandler<String>(1));

        } catch ( SQLException  e) {
            e.printStackTrace();
        }

        return results;
    }


}
