package learn.eth.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;

public class CryptoTest extends CryptoBase {

    private final Logger logger = LoggerFactory.getLogger("Just Enough Crypto Test");
    private static final String MENOMIC = "Ths is a set of words that maybe you can remember";
    private static final String MENOMIC_2 = "This is a set of words that maybe you can remember";

    /**
     * A hash is a digest is a checksum
     */
    @Test
    public void HashTest() {

        // Keccak-256 hash function
        String hash = Hash.sha3String(MENOMIC);
        logger.info("Keccak-256 hash: {} ", hash);

        // small changes to input yields big changes in the value of the hash
        String hash_avalanche = Hash.sha3String(MENOMIC_2);
        logger.info("Keccak-256 hash: {} ", hash_avalanche);
        // prefix
        logger.info("Keccak-256 hash no prefix: {} ",  Numeric.cleanHexPrefix(hash_avalanche));

    }

    @Test
    public void asymetricEncryptionTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger pubKey = ecKeyPair.getPublicKey();
        BigInteger priKey = ecKeyPair.getPrivateKey();
        logger.info("ECKeyPair private: {} ", priKey);
        logger.info("ECKeyPair public: {} ", pubKey);

        BigInteger factor = pubKey.divide(priKey);
        logger.info("ECKeyPair factor: {} ", factor);

        BigInteger product = priKey.multiply(factor);

        logger.info("ECKeyPair product: {} ", product);

        // if I could factor P * Q into P and Q, then I could re-derive the private key.
        // its a logarithmic relationship
    }

    @Test
    public void cipherDemoTest() {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPair keyPairA = generateECKeys();
        SecretKey secretKeyA = generateSharedSecret(keyPairA.getPrivate(), keyPairA.getPublic());

        String cipherText = encryptString(secretKeyA, MENOMIC_2);
        logger.info("cipherText: {} ", cipherText);

        String decryptedPlainText = decryptString(secretKeyA, cipherText);

        logger.info("decryptedPlainText: {} ", decryptedPlainText);

    }


    /**
     * For a given private key, pr, the Ethereum address A(pr) (a 160-bit value) derived  the right most 160-bits of the Keccak hash of
     * the corresponding ECDSA public key
     */

    @Test
    public void addressTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger pubKey = ecKeyPair.getPublicKey();
        logger.info("ECKeyPair public: {} ", pubKey.toString(16));
        String hash = Hash.sha3(pubKey.toString(16));
        logger.info("ECKeyPair Hex  address: {} ", hash.substring(26)); // corresponds to the last 160 bits
        String hexAddress = Keys.getAddress(pubKey);
        logger.info("ECKeyPair Hex  address: {} ", hexAddress);

        String hexAddressWithPrefix = Numeric.toHexStringWithPrefixZeroPadded(new BigInteger(hexAddress, 16), hexAddress.length());
        logger.info("ECKeyPair Hex  hexAddressWithPrefix: {} ", hexAddressWithPrefix);

    }


    @Test
    public void signingTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

        byte[] data = MENOMIC_2.getBytes();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        Sign.SignatureData signatureData = Sign.signMessage(data, ecKeyPair);
        BigInteger pubKey = Sign.signedMessageToKey(data, signatureData);
        logger.info("ECKeyPair signatureData: {} ", pubKey.toString(16));

    }


}
