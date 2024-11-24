package org.library.testMigration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.connection.ConnectionManager;
import org.library.migration.MigrationManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.mockito.Mockito.*;

/**
 * Class for testing receiving versions and migrations;
 */
@ExtendWith(MockitoExtension.class)
public class TestMigrationManager {


    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private MigrationManager migrationManager;

    @BeforeEach
    void setUp() {
        migrationManager =  migrationManager.getInstance();
    }

    @Test
    @DisplayName("Test getting current version from db")
    public void testCurrentVersion() throws SQLException {

        mockStatic(ConnectionManager.class);
        when(ConnectionManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getString("version")).thenReturn("2");
        String version=migrationManager.getLastVersion();
        Assertions.assertEquals(version,"2");

        verify(mockConnection,times(1)).createStatement();
        verify(mockStatement,times(1)).executeQuery(anyString());
        verify(mockResultSet,times(1)).next();
        verify(mockResultSet,times(1)).getString("version");

    }

}
