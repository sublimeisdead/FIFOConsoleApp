package ru.sublimeisdead.FIFOConsoleApp;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final String DB_URL="jdbc:h2:file:~/MyWarehouse";
    private final String USERNAME="sa";
    private final String PASSWORD="";

    public DatabaseConnection() throws SQLException{
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if(instance==null){
            instance=new DatabaseConnection();
        }else if(instance.getConnection().isClosed()){
            instance=new DatabaseConnection();
        }

        return instance;
    }
}
