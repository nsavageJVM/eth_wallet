package learn.eth.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.web3j.crypto.Credentials;
import rx.Subscriber;

@Configuration
public class BeansConfig {

    private final Logger logger = LoggerFactory.getLogger("home grown beans");



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
