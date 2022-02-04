package com.logitex.notifierbot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "percoEntityManagerFactory",
        basePackages = {"com.logitex.notifierbot.repository.perco"},
        transactionManagerRef = "percoTransactionManager"
)
public class PercoDataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.perco")
    public DataSourceProperties percoDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.perco.configuration")
    public DataSource percoDataSource() {
        return percoDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "percoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean percoEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(percoDataSource())
                .packages("com.logitex.notifierbot.model.perco")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager percoTransactionManager(
            final @Qualifier("percoEntityManagerFactory") LocalContainerEntityManagerFactoryBean percoEntityManagerFactory
    ) {
        return new JpaTransactionManager(percoEntityManagerFactory.getObject());
    }
}
