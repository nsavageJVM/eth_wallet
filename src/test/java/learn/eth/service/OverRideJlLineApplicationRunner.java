package learn.eth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@TestConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OverRideJlLineApplicationRunner implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger("overide JLine for this test");

    public OverRideJlLineApplicationRunner() {
        log.info("Test Application Runner started!");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("About to do nothing!");
        // Do nothing...
    }

}
