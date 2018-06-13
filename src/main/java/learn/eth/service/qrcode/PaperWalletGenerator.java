package learn.eth.service.qrcode;

import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


@Service
public class PaperWalletGenerator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String user_home = System.getProperty("user.home");


    String OUT_DIR = String.format("%s/paper_wallets", user_home );
    String OUT_FILE = String.format("%s/paper_wallets/test.pdf", user_home );
    String TEMPLATES_IN_DIR = "classpath:paperwallet/report.jrxml";

    @Autowired
    BitMapGenerator qrCodeGenerator;



    public Boolean runJasperPaperWalletFlow(Credentials creds)  {
        createReportOutputDirectory();
        Resource tempalate_home = new ClassPathResource(TEMPLATES_IN_DIR);
        BufferedImage qrCodeAddress = null;
        BufferedImage qrCodePubKey = null;
        BufferedImage qrCodePriKey = null;

        String template =null;

        JREmptyDataSource ds = new JREmptyDataSource();
        try {
            template =  loadClassPathData().toString();
            qrCodeAddress = qrCodeGenerator.createQRCode(creds.getAddress());
            qrCodePubKey = qrCodeGenerator.createQRCode(creds.getEcKeyPair().getPublicKey().toString(16));
            qrCodePriKey = qrCodeGenerator.createQRCode(creds.getEcKeyPair().getPrivateKey().toString(16) );
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("title", "Paper Wallet");
            params.put("qraddress" , qrCodeAddress );
            params.put("qrpubkey" , qrCodePubKey );
            params.put("qrprikey" , qrCodePriKey );
            JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream( template.getBytes(Charset.defaultCharset())));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params , ds);
            JasperExportManager.exportReportToPdfFile(jasperPrint, OUT_FILE);


        } catch (WriterException |IOException | JRException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void createReportOutputDirectory() {

        File file = new File(OUT_DIR);
        if (!file.exists()) {
            if (file.mkdir()) {
                logger.debug("OUT Directory is created!");
            } else {
                logger.debug("Failed to create OUT directory!");
            }
        }
    }

    private List<File> getFileSystemResources(Resource  res ) throws IOException {
        List<File> fileList = new ArrayList<>();

        //uses try-with-resources pattern ensures stream will be closed.
        try(Stream<Path> paths = Files.walk(Paths.get(res.getURL().getPath()))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {

                    fileList.add(filePath.toFile());
                }
            });
        }
        return  fileList ;
    }


    private StringBuilder loadClassPathData( ) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources(TEMPLATES_IN_DIR);

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resources[0].getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result;
    }

}
