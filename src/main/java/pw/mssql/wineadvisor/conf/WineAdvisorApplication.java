package pw.mssql.wineadvisor.conf;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import pw.mssql.wineadvisor.service.KnowledgeBaseService;
import pw.mssql.wineadvisor.service.impl.KnowledgeBaseServiceImpl;

import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "pw.mssql.wineadvisor")
@PropertySource(value = {"classpath:db.properties"})
public class WineAdvisorApplication {

    @Autowired
    private Environment env;

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

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc.drivers"));
        dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
        return dataSource;
    }
}
