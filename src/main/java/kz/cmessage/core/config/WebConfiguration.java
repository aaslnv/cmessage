package kz.cmessage.core.config;

import kz.cmessage.core.web.filter.RequestLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.Arrays;

@Slf4j
@Configuration
public class WebConfiguration implements ServletContextInitializer {

    private Environment env;

    public WebConfiguration(Environment env) {
        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        }
    }

    @Bean
    public Filter logFilter() {
        CommonsRequestLoggingFilter filter = new RequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(5120);
        filter.setIncludeClientInfo(true);
        return filter;
    }
}
