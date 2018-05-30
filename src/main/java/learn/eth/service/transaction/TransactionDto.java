package learn.eth.service.transaction;

import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigInteger;

public class TransactionDto {

    private BigInteger nonce;

    private String remoteAccount;

    private Credentials localCredentials;

    private BigInteger amountWei;

    public TransactionDto( ) {

    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getGasPrice() {
        return Convert.toWei("2", Convert.Unit.GWEI).toBigInteger();
    }

    public BigInteger getGasLimit() {
        return BigInteger.valueOf(21000);
    }


    public String getRemoteAccount() {
        return remoteAccount;
    }

    public void setRemoteAccount(String remoteAccount) {
        this.remoteAccount = remoteAccount;
    }

    public String getLocalAccount() {
        return localCredentials.getAddress();
    }

    public Credentials getLocalCredentials() {
        return localCredentials;
    }

    public void setLocalCredentials(Credentials localCredentials) {
        this.localCredentials = localCredentials;
    }

    public BigInteger getAmountWei() {
        return amountWei;
    }

    public void setAmountEther(String amountEther) {
        this.amountWei = Convert.toWei(amountEther, Convert.Unit.ETHER).toBigInteger();
    }
}
