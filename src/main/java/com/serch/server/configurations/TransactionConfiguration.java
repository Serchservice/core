package com.serch.server.configurations;

import com.atomikos.jdbc.internal.AbstractDataSourceBean;
import com.atomikos.spring.AtomikosDataSourceBean;
import lombok.RequiredArgsConstructor;
import org.postgresql.xa.PGXADataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class TransactionConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TransactionConfiguration.class);
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username:#{null}}")
    private String username;

    @Value("${spring.datasource.password:#{null}}")
    private String password;

//    @Bean(name = "userTransaction")
//    public UserTransaction userTransaction() throws Throwable {
//        UserTransactionImp userTransactionImp = new UserTransactionImp();
//        userTransactionImp.setTransactionTimeout(10000);
//        return userTransactionImp;
//    }

    @Bean("securityCheckDataSource")
    public AbstractDataSourceBean securityCheckDataSource(Environment env) {
        String profile;
        try {
            profile = env.getActiveProfiles()[0]; // Get the active profile
        } catch (Exception e) {
            profile = "default";
        }
        log.info(String.format("CURRENT PROFILE::: %s", profile));

        PGXADataSource pg = getPgxaDataSource(profile);

        AtomikosDataSourceBean source = new AtomikosDataSourceBean();
        source.setUniqueResourceName("SJTADB");
        source.setMaxPoolSize(200);
        source.setXaDataSource(pg);

        return source;
    }

    private PGXADataSource getPgxaDataSource(String profile) {
        String finalUrl;
        String finalUser;
        String finalPassword;

        if ("dev".equals(profile)) {
            finalUrl = url;
            finalUser = username;
            finalPassword = password;
        } else {
            // Extract the user, password, and URL from the connection string for sandbox or production
            String[] urlParts = url.split("\\?");
            finalUrl = urlParts[0];

            String[] params = urlParts[1].split("&");
            finalUser = params[0].split("=")[1];
            finalPassword = params[1].split("=")[1];
        }

        PGXADataSource pg = new PGXADataSource();
        pg.setUrl(finalUrl);
        pg.setUser(finalUser);
        pg.setPassword(finalPassword);
        return pg;
    }
}
