package pw.mssql.wineadvisor.conf;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import pw.mssql.wineadvisor.service.KnowledgeBaseService;
import pw.mssql.wineadvisor.service.impl.KnowledgeBaseServiceImpl;

@SpringBootApplication(scanBasePackages = "pw.mssql.wineadvisor")
public class WineAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WineAdvisorApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean corsFilterRegistration() {

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

    @Bean
    public KnowledgeBaseService classificationService() throws Exception {
        return new KnowledgeBaseServiceImpl();
    }
}
