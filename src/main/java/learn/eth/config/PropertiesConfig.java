package learn.eth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wallet")
public class PropertiesConfig {


    private String base;

    private String base_template;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase_template() { return base_template; }

    public void setBase_template(String base_template) {
        this.base_template = base_template;
    }




}
