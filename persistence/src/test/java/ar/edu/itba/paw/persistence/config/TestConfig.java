package ar.edu.itba.paw.persistence.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@ComponentScan({"ar.edu.itba.paw.persistence"})
@Configuration
@EnableTransactionManagement
public class TestConfig {

    @Value("classpath:hsqldb.sql") // Le pedimos que nos traiga este archivo en resources
    private Resource hsqldbSql; // (leer el comentario enorme en dataSource() para entender esto

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.hsqldb.jdbcDriver.class);
        ds.setUrl("jdbc:hsqldb:mem:pawtest");
        ds.setUsername("ha");
        ds.setPassword("");

        return ds;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(databasePopulator());

        return dsi;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(hsqldbSql);
        populator.addScript(schemaSql);
        return populator;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final DataSource ds) {

        return new DataSourceTransactionManager(ds);
    }
}