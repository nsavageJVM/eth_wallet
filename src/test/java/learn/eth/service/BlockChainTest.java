package learn.eth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.eth.EntryPoint;
import learn.eth.chain.Block;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntryPoint.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class BlockChainTest   {

    private final Logger logger = LoggerFactory.getLogger("BlockChain Test");

    @Autowired
    ApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    public static final  List<Block> blockchain = new LinkedList<Block>();

    @Before
    public void setUp() {

        Block genesisBlock = (Block) context.getBean("block", "First block", "0");
        logger.info("Hash for block 1: {}", genesisBlock.hash);

        Block secondBlock = (Block) context.getBean("block","Second block", genesisBlock.hash);
        logger.info("Hash for block 2: {}", secondBlock.hash);

        Block thirdBlock = (Block) context.getBean("block","Third block", secondBlock.hash);
        logger.info("Hash for block 2: {}", thirdBlock.hash);

        blockchain.add(genesisBlock);
        blockchain.add(secondBlock);
        blockchain.add(thirdBlock);

    }


    @Test
    public void runBlockChainAsTest() throws Exception {

        String jsonInString = objectMapper.writeValueAsString(blockchain);
        logger.info("blockchain: {}", jsonInString);


        for(Block block : blockchain) {
            block.mineBlock(Block.difficulty, block);
        }

        logger.info("Blockchain is Valid: " + Block.isChainValid(blockchain));

    }

}
