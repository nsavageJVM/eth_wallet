package learn.eth.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.shell.jline.PromptProvider;
import org.web3j.crypto.Credentials;

import rx.Subscriber;


@Configuration
public class BeansConfig {

    private final Logger logger = LoggerFactory.getLogger("home grown beans");

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("wally's wallets:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
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
