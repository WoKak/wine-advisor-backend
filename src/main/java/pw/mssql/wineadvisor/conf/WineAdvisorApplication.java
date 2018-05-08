package pw.mssql.wineadvisor.conf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "pw.mssql.wineadvisor")
public class WineAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WineAdvisorApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public CustomCorsFilter corsFilter() {
        return new CustomCorsFilter();
    }
}
