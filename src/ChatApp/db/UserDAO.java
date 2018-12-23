package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.Role;
import ChatApp.core.User;
import ChatApp.core.Msg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

// DAOs are singleton. Private constructor
// Call them through getInstance() to get the same object always

public class UserDAO extends Database {

    public static UserDAO userDAO = null;

    private UserDAO(){ }

    public static UserDAO getInstance() {
        if (userDAO == null)
            userDAO = new UserDAO();
        return userDAO;
    }

    public int checkUser(String username){
        int userID = 0;

        String query = "SELECT * FROM `users` WHERE `username` = \'" +username + "\';";

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        for (Map<String,Object> row: answer) {
            userID = (Integer) row.get("id");
    }

    return userID;
}

    public boolean checkPass(int userID, String pass){
        boolean userAuth = false;
        int newID = 0;

        String query = "SELECT `id` FROM `users` WHERE `id` = '" +userID + "' AND `pass` = '"+ pass+"';";

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        for (Map<String,Object> row: answer) {
            newID = (Integer) row.get("id");
        }
        return newID != 0;
    }

    public void setUser(User curUser){

        String query =  "SELECT * FROM `users` "+
                "WHERE `id` = '" + curUser.getID() + "';";

        //Execute the query and Get the ArrayList of maps (the ROWS!)
        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        //Loop through the rows and get values
        for (Map<String,Object> row: answer) {
            curUser.setID( (Integer) row.get("id"));
            curUser.setUsername( (String) row.get("username"));
            curUser.setRoleID( (Integer) row.get("role_id"));
        }

}



    public boolean toBool(String a){
            return (a.equals("1")? true: false );
    }

    public int createUser(String username, String pass) {
        Connection conn = createConnection();
        PreparedStatement prest = null;
        int rowsInserted = 0;
        String query =  "INSERT INTO `users` (`username`,`pass`,`role_id`)"+
                        "VALUES (?,?,?);";
        try {
            prest = conn.prepareStatement(query);
            prest.setString(1,username);
            prest.setString(2,pass);
            prest.setInt(3,2);  //create a user with reader role
            rowsInserted = prest.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsInserted;

    }

    public ArrayList<User> searchForUser(String keyword){
        String query =  "SELECT * FROM `chat02`.`users` "+
                "WHERE `username` LIKE '%" + keyword + "%';";

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        ArrayList<User> usersFound = new ArrayList<>();

        for (Map<String,Object> row: answer){
            User user = new User();
            user.setID( (Integer) row.get("id"));
            user.setUsername( (String) row.get("username"));
            user.setRoleID( (Integer) row.get("role_id"));
            usersFound.add(user);
        }

        return usersFound;
    }

    public HashMap<Integer, User> selectAllUsers(){
        String query =  "SELECT * FROM `chat02`.`users`;";

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        HashMap<Integer, User> usersFound = new HashMap<>();

        for (Map<String,Object> row: answer){
            User user = new User();
            user.setID( (Integer) row.get("id"));
            user.setUsername( (String) row.get("username"));
            user.setRoleID( (Integer) row.get("role_id"));
            usersFound.put( user.getID() , user );
        }

        return usersFound;
    }




}
