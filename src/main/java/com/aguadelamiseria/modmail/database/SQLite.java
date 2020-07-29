package com.aguadelamiseria.modmail.database;

import com.aguadelamiseria.modmail.Modmail;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends DataHandler {

    private final String HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `history_table` ("+
            "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
            "`user` TEXT NOT NULL, "+
            "`staff` TEXT NOT NULL, "+
            "`date` TEXT NOT NULL, "+
            "`reason` TEXT)";

    public SQLite(Modmail modmail) {
        super(modmail);
        try {
            this.connection = getSQLConnection();
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(HISTORY_TABLE);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private Connection getSQLConnection() {
        File dataFolder = new File(System.getProperty("user.dir"), "data.db");
        if (!dataFolder.exists())
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            if (connection != null && !connection.isClosed())
                return connection;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException|ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected Connection getConnection() {
        try {
            if (connection.isClosed()) {
                refreshConnection();
                return connection;
            }
            return connection;
        } catch (Exception ex) {
            ex.printStackTrace();
            return connection;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshConnection() {
        try {
            if (getConnection() != null)
                getConnection().close();
            getSQLConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
