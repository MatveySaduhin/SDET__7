package org.example;

import java.sql.*;

public class DbApp {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();
            createTableCars();
            clearTableCars();

            insertSomeCars();
            readCars();

            deleteWhiteCars();
            System.out.println("After delete:");
            readCars();

            clearTableCars();
            psBatchCars();
            transactionCars();
            System.out.println("After psBatch:");
            readCars();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void connect() throws SQLException {
        System.out.println("Открывается соединение с БД");
        connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
        statement = connection.createStatement();
    }

    private static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Соединение закрыто");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableCars() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS cars (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " registration_id TEXT,\n" +
                " color TEXT\n" +
                " );");
    }

    private static void dropTableCars() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS cars;");
    }

    private static void readCars() throws SQLException {
        try (ResultSet rs = statement.executeQuery("SELECT * FROM cars;")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString("registration_id") + " " +
                        rs.getString("color"));
            }
        }
    }

    private static void clearTableCars() throws SQLException {
        statement.executeUpdate("DELETE FROM cars;");
    }

    private static void deleteWhiteCars() throws SQLException {
        statement.executeUpdate("DELETE FROM cars WHERE color = 'white';");
    }

    private static void insertSomeCars() throws SQLException {
        statement.executeUpdate("INSERT INTO cars (registration_id, color) VALUES ('M632PB', 'yellow');");
        statement.executeUpdate("INSERT INTO cars (registration_id, color) VALUES ('T777YM', 'white');");
        statement.executeUpdate("INSERT INTO cars (registration_id, color) VALUES ('M333MM', 'white');");
        statement.executeUpdate("INSERT INTO cars (registration_id, color) VALUES ('P567OC', 'red');");
    }

    private static void psBatchCars() {
        try (PreparedStatement prepInsert = connection.
                prepareStatement("INSERT INTO cars(registration_id,color) VALUES(?,?)")) {
            for (int i = 0; i <= 9; i++) {
                prepInsert.setString(1, "O000O" + i);
                prepInsert.setString(2, "just_grey");
                prepInsert.addBatch();
            }
            int[] result = prepInsert.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void transactionCars() throws SQLException {
        connection.setAutoCommit(false);
        try {
            statement.execute("INSERT INTO cars (registration_id, color) values ('H732HO', 'black')");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        }
    }

}
