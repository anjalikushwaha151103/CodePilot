package com.codepilot.common;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private DataSource dataSource;

    @Test
    void flywayBeanShouldBeAvailable() {
        assertNotNull(flyway, "Flyway bean should be available in the application context");
    }

    @Test
    void migrationsShouldBeApplied() {
        var info = flyway.info();
        assertNotNull(info.current(), "At least one migration should be applied");
    }

    @Test
    void usersTableShouldExist() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "USERS", new String[]{"TABLE"});
            assertTrue(tables.next(), "The 'users' table should exist after Flyway migration");
        }
    }
}
