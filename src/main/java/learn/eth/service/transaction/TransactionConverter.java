package learn.eth.service.transaction;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.web3j.crypto.RawTransaction;

@Component
public class TransactionConverter  implements Converter<TransactionDto, RawTransaction> {


    @Override
    public RawTransaction convert(TransactionDto source) {
        RawTransaction rawTransaction
                = RawTransaction
                .createEtherTransaction(
                        source.getNonce(),
                        source.getGasPrice(),
                        source.getGasLimit(),
                        source.getRemoteAccount(),
                        source.getAmountWei());

        return rawTransaction;
    }
}
