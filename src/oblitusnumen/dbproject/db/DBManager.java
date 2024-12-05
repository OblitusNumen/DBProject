package oblitusnumen.dbproject.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {
    private static final String DB_FILENAME = "./database.db";
    public static final String URL = "jdbc:sqlite://" + DB_FILENAME;

    public DBManager() throws SQLException {
        File file = new File(DB_FILENAME);
        if (!file.exists()) {
            init();
        }
    }

    private void init() throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("create table if not exists")) {// FIXME: 11/24/24
                if (!statement.execute()) {
                    throw new SQLException("couldn't create table");
                }
            }// TODO: 11/24/24 tables
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public int getTableWidth(String tableName) {// FIXME 12/5/24 9:36PM
        throw new RuntimeException();
    }
}
