package learn.eth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wallet")
public class PropertiesConfig {

    private String base;
    private String db;
    private String template;

    public String getBase() { return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTemplate() { return template;
    }

    public void setTemplate(String template) { this.template = template;
    }

    public String getDb() { return db;
    }

    public void setDb(String db) { this.db = db;
    }






}
