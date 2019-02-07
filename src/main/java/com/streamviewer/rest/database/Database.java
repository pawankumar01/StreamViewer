package com.streamviewer.rest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    /**
     * Local Setting
     */
    public static final String DB_NAME = "notes";
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
    public static final String DB_USERNAME = "pawankumar";
    public static final String DB_PASSWORD = "";

    /**
     * PROD Settings
     */
//    public static final String DB_NAME = "streamviewer";
//    public static final String DB_URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;;
//    public static final String DB_USERNAME = "postgres";
//    public static final String DB_PASSWORD = "teststream";

    protected Connection db = null;

    /**
     * Checks PostgreSQL driver.
     *
     * @throws
     */
    public void checkPostgreDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find PostgreSQL JDBC Driver? Please include in library path.");
            e.printStackTrace();
        }
    }

    public void connect() {
        checkPostgreDriver();

        try {
            db = DriverManager.getConnection(Database.DB_URL, Database.DB_USERNAME, Database.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (db == null) {
            System.out.println("Failed to make connection!");
        }
    }

    public void close() {
        if (db == null) {
            return;
        }
        try {
            if (!db.isClosed()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
