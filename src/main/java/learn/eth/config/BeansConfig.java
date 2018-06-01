package learn.eth.config;

import learn.eth.service.shell.TransactionConverter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
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

    @Bean(name = "transaction")
    public ConversionService getConversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(getConverters());
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }

    private Set<Converter<?, ?>> getConverters() {
        Set<Converter<?, ?>> converters = new HashSet<Converter<?, ?>>();
        converters.add(transactionConverter);
        return converters;
    }






    @Bean
    public ExitCodeExceptionMapper getExitCodeExceptionMapper() {

        ExitCodeExceptionMapper mapper = new ExitCodeExceptionMapper() {
            @Override
            public int getExitCode(Throwable exception) {
                System.out.println("oops" +exception.getLocalizedMessage() );
                return 0;
            }
        };

        return mapper;
    }





}
