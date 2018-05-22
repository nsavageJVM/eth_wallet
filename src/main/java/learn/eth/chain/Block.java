package learn.eth.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Hash;

import java.util.Date;
import java.util.List;

@Component("block")
@Scope("prototype")
public class Block {

    private static final Logger logger = LoggerFactory.getLogger("blockchain");

    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;
    public int nonce;
    public static String difficulty = "4";


    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = createKeccak256Hash();
    }


    String createKeccak256Hash() {
        return Hash.sha3String(previousHash + Long.toString(timeStamp) + data);

    }


    public void mineBlock(String difficulty, Block block) {


        while (!block.hash.contains(difficulty)) {

            block.hash = createKeccak256Hash();
            block.nonce++;
        }
        logger.info("Block Mined: ", block.hash);
    }


    public static Boolean isChainValid(List<Block> blockchain) {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                logger.error("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }


}
