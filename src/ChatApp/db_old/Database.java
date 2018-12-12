package ChatApp.db_old;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Database {
    private static final String DRIVER_DIR = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "cdn.eletter.gr:3325";
    private static final String DB_URL2 = "localhost:3306";
    private static final String DATABASE = "chat02";   //chatapp
    private static final String PARAMETERS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String FULL_DB_URL = "jdbc:mysql://" + DB_URL2 + "/" + DATABASE + PARAMETERS;
    //?zeroDateTimeBehavior=convertToNull;
    //?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
    private static final String DB_USER = "takis";
    private static final String DB_PASSWD = "Tr1gk1$";


    public void registerDriver() {
        //Register JDBC Driver
        try {
            Class.forName(DRIVER_DIR);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }
        System.out.println("MySQL JDBC Driver Registered!");
    }

    public Connection createConnection() {
        Connection connection = null;
        ResultSet resultSet = null;

        registerDriver();
        //CHECK Connection
        try {
            connection = DriverManager.getConnection(FULL_DB_URL, DB_USER, DB_PASSWD);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }


        return connection;
    }




//    public ResultSet execSelect(Connection conn, String query) {
//        ResultSet rs = null;
//        Statement st = null;
//        try {
//            st = conn.createStatement();
//            rs = st.executeQuery(query);
//        }
//        catch (SQLException | NullPointerException ex) {
//            System.out.println("Execute query did not work!");
//        }
//        return rs;
//    }





//
//    public boolean createUser() {
//        String sqlCreateUser = null;
//        sqlCreateUser = new StringBuilder()
//            .append("INSERT INTO `users` (`username`, `pass`, `lname`, `fname`, `role_id`, `reg_date`, `active`) VALUES ")
//            .append("('admin', 'admin', 'admin', 'admin', '0' , 'true') ")
//            .append("('george', '2410', 'Giorgos', 'Papado', '1' , 'true');\n").toString();
//
//        if (createConnection()) {
//            insertQuery(sqlCreateUser);
//        }
//    }
//
//    public boolean readUser() {
//        return true;
//    }


    public boolean connectAndRead(String query, boolean bPrint) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        if (query.isEmpty()) query = "SELECT id, username FROM users";
        System.out.println(query);

        try {
//           Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(FULL_DB_URL, DB_USER, DB_PASSWD);
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    if(bPrint) System.out.printf("%s\t%s\n", resultSet.getString(1), resultSet.getString(2));
                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch (SQLException | NullPointerException ex) {
                System.out.println("query did not work!");
            }

        } catch (SQLException ex) {
            System.out.println("Ooops! No working database!");
            //| ClassNotFoundException
        }

        return false;
    }


    public boolean readFromTable(String table, String[] cols){
        String query = "SELECT `";
        for(String col: cols ){
            query += col + "`, `";
        }
        query = query.substring(0,query.length()-3);
        query += "FROM `"+ table + "`;";
        connectAndRead(query, true);
        return true;
    }

    public boolean readTable(String table){
        table = "SELECT * FROM `"+ table + "`;";
        connectAndRead(table, true);
        return true;
    }


}
