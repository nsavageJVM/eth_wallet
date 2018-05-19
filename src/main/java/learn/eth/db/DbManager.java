package learn.eth.db;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Class.forName;

@Component
public class DbManager {

    private Connection connection = null;
    private static Boolean isInitial = false;

    private static final String FILE_PATH_TEMPLATE = "/home/ubu/wally/%s";

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
            connection = DriverManager.getConnection("jdbc:sqlite:/home/ubu/wally.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);  // set timeout to 3 sec.

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
            run.update(connection, "insert into wally values (?,?)",
                    new Object[]{menomic, file_path});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Credentials getUserCredentials(String menomic) {
        Credentials  credentials = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM wally where menomic = '" + menomic + "';");

            while (rs.next()) {
                String file_path = rs.getString("file_path");

                    credentials = WalletUtils.loadCredentials(menomic, String.format(FILE_PATH_TEMPLATE, file_path ) );

            }

        } catch (IOException | SQLException | CipherException e) {
            e.printStackTrace();
        }
        return credentials;
    }





}
