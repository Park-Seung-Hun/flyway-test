package com.example.flyway.flywaytest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.database.DatabaseTypeRegister;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;

public class Flyway3168Test {

    private DataSource dataSource;

    @BeforeEach
    public void initDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.tmax.tibero.jdbc.TbDriver");
        hikariConfig.setJdbcUrl("jdbc:tibero:thin:@192.168.14.109:8630:tb_oflab");
        hikariConfig.setUsername("tibero");
        hikariConfig.setPassword("tmax");
        dataSource = new HikariDataSource(hikariConfig);
    }

    private void migrate() {
        Flyway.configure(getClass().getClassLoader())
                .locations("classpath:test-migrations") // Uses some migrations in src/test/resources/test-migrations
                .dataSource(dataSource)
                .load().migrate();
    }

    private void migrateUsing(ClassLoader contextClassLoader) {
        Thread currentThread = Thread.currentThread();
        ClassLoader originalLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(contextClassLoader);
        try {
            migrate();
        } finally {
            currentThread.setContextClassLoader(originalLoader);
        }
    }

    // PASSES always
    @Test
    public void flywayVisibleFromContextLoader() {
        migrateUsing(getClass().getClassLoader());
    }

    // PASSES on 7.7.1, FAILS on 7.8.2
    @Test
    public void flywayNotVisibleFromContextLoader() {
        ClassLoader emptyLoader = new ClassLoader(null) {};
        migrateUsing(emptyLoader);
    }

    @AfterEach
    public void resetFlywayState() throws NoSuchFieldException, IllegalAccessException {
        // No other good way to do this
        Field field = DatabaseTypeRegister.class.getDeclaredField("hasRegisteredDatabaseTypes");
        field.setAccessible(true);
        field.set(null, false);
    }

}