package ChatApp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    protected static Class dbClass;
    protected String server = "localhost:3306";
    protected static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    protected static final String PREFACE = "jdbc:mysql://";
    protected String database = "chat02";
    protected String username = "root";
    protected String password = "kiriosroot";
    protected String options = "?zeroDateTimeBehavior=convertToNull&serverTimezone=Europe/Athens&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false";
    protected String urlDB;

 //Method create URL for connecting
 public void createURL(){
     urlDB = ( PREFACE + server + "/" + database + options);
 }

public void registerDriver() {
    try {
        Class.forName(DRIVER);
    } catch (ClassNotFoundException e) {
        System.out.println("Driver not registered!");
        e.printStackTrace();
    }
}

 //Method creating connection to DB
public Connection createConnection(){
    Connection conn = null;
    registerDriver();
    createURL();

    try {
        conn = DriverManager.getConnection(urlDB,username,password);
    } catch (SQLException e) {
        e.printStackTrace();
    }

    System.out.println((conn != null)?"You made it, take control your database now!":"Failed to make connection!");

    return conn;
}

//Method execute UPDATE or INSERT
public int execUpdateInsert(Connection conn, String query) {
    PreparedStatement prestm = null;
    int rowsAffected = 0;

    try {
        prestm = conn.prepareStatement(query);
        rowsAffected = prestm.executeUpdate(query);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rowsAffected;
}

//Method execute SELECT
public ResultSet execSelect(Connection conn, String query) {
    ResultSet rs = null;
    Statement st = null;
    try {
        st = conn.createStatement();
        rs = st.executeQuery(query);
    }
    catch (SQLException | NullPointerException ex) {
        System.out.println("Execute query did not work!");
    }
    return rs;
}



}
