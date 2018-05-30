package learn.eth.config;

import learn.eth.service.shell.TransactionConverter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.shell.jline.PromptProvider;
import org.web3j.crypto.Credentials;

import rx.Subscriber;

import java.util.HashSet;
import java.util.Set;


@Configuration
public class BeansConfig {

    private final Logger logger = LoggerFactory.getLogger("home grown beans");

    @Autowired
    private TransactionConverter transactionConverter;

    @Bean(name="transaction")
    public ConversionService getConversionService()  {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters( getConverters() );
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }

    private Set<Converter<?, ?>> getConverters()  {
        Set<Converter<?, ?>> converters = new HashSet<Converter<?, ?>>();
        converters.add( transactionConverter );
        return converters;
    }

    /**
     * A shutdown hook from a callback
     * @return
     */
    @Bean(name="shutDownHook")
    public Subscriber<String> getShutDownHook()  {
        return  new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.exit(0);
            }

            @Override
            public void onError(Throwable throwable) { }

            @Override
            public void onNext(String s) {
                logger.info(s);

            }
        };

    }


    @Bean(name="credentialsHook")
    public Subscriber<Credentials> getCredentialsHook()  {
        return  new Subscriber<Credentials>() {
            @Override
            public void onCompleted() {
                System.exit(0);
            }

            @Override
            public void onError(Throwable throwable) { }

            @Override
            public void onNext(Credentials s) {
                logger.info("local wallet address: {}", s.getAddress());

            }
        };

    }
}
