package com.logitex.notifierbot.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "botEntityManagerFactory",
        basePackages = {"com.logitex.notifierbot.repository.bot"},
        transactionManagerRef = "botTransactionManager"
)
public class BotDataSourceConfig {
    @Bean
    @ConfigurationProperties("app.datasource.bot")
    public DataSourceProperties botDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.bot.configuration")
    public DataSource botDataSource() {
        return botDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean(name = "botEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean botEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(botDataSource())
                .packages("com.logitex.notifierbot.model.bot")
                .build();
    }

    @Bean
    public PlatformTransactionManager botTransactionManager(
            final @Qualifier("botEntityManagerFactory") LocalContainerEntityManagerFactoryBean botEntityManagerFactory
    ) {
        return new JpaTransactionManager(botEntityManagerFactory.getObject());
    }
}
