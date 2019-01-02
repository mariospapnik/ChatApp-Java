package ChatApp.app.db;

import ChatApp.app.core.Role;
import ChatApp.app.core.User;
import ChatApp.app.db.dbcore.Database;
import ChatApp.app.log.ChatLog;

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
        //Log the activity
        ChatLog.logAcivity("Log in attempt with username: " + username);

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

        //Log the activity
        ChatLog.logAcivity( ( (newID==0)?"Unsuccessful": "Successful" ) + " log in attempt for user with ID : " + userID);

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
            curUser.setRole( new Role( (Integer) row.get("role_id")) );
        }

        //Log the activity
        ChatLog.logAcivity( "Retrieved info for the user: " + curUser.getUsername());

}

    public int createUser(String username, String pass, String fname, String lname) {
        Connection conn = createConnection();
        PreparedStatement prest = null;
        int rowsInserted = 0;
        String query =  "INSERT INTO `users` (`username`,`pass`,`role_id`,`fname`,`lname`)"+
                        "VALUES (?,?,?,?,?);";
        try {
            prest = conn.prepareStatement(query);
            prest.setString(1,username);
            prest.setString(2,pass);
            prest.setInt(3,2);  //create a user with reader role
            prest.setString(4,fname);
            prest.setString(5,lname);
            rowsInserted = prest.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Log the activity
        ChatLog.logAcivity("User with details: Username: " + username + "| FirstName: " + fname + " | LastName: " + lname + "\n"
                    + ((rowsInserted==0)?"unsuccessfuly": "successfuly") + "inserted");

        return rowsInserted;

    }

    public HashMap<Integer, User> searchForUser(String keyword){
        String like = "'%" + keyword + "%'";

        String query =  "SELECT * FROM `users` "+
                "WHERE `username` LIKE " + like +
                " OR `fname` LIKE " + like +
                " OR `lname` LIKE " + like + ";";

        //Log the activity
        ChatLog.logAcivity( "Search for users using '" + keyword + "' keyword." );

        return getUsersfromQuery(query);
    }

    public HashMap<Integer, User> selectAllUsers(){

        String query =  "SELECT * FROM `users`;";
        return getUsersfromQuery(query);
    }

    public HashMap<Integer, User> getUsersfromQuery(String query){

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        HashMap<Integer, User> usersFound = new HashMap<>();

        for (Map<String,Object> row: answer){
            User user = new User();
            user.setID( (Integer) row.get("id"));
            user.setUsername( (String) row.get("username"));
            user.setRole(new Role( (Integer) row.get("role_id") ));
            user.setFname((String) row.get("fname"));
            user.setLname((String) row.get("lname"));
            user.setRegDate( (Date) row.get("create_date"));
            user.setActive( ((Integer) row.get("active")) == 1 );
            usersFound.put( user.getID() , user );
        }

        //Log the activity
        ChatLog.logAcivity( usersFound.size()  + " users where retrieved");

        return usersFound;
    }

    public int toogleUser(User user) {
        int userToogled = 0;

        int toogle = (user.getActive())? 0 : 1;

        String query = "UPDATE `users` SET `active` = '"+ toogle + "' WHERE `id` = '" + user.getID() + "';";
        userToogled = execUpdateInsert(query);

        //Log the activity
        ChatLog.logAcivity( ((userToogled==1)?"Successfully":"Unsuccessfully")
                + " toggled user '" + user.getUsername() + "' .");

        return userToogled;
    }

    public int updateUserRole(User user, int role) {

        int userRoleUpdated = 0;

        String query = "UPDATE `users` SET `role_id` = '"+ role + "' WHERE `id` = '" + user.getID() + "';";
        userRoleUpdated = execUpdateInsert(query);

        //Log the activity
        ChatLog.logAcivity( ((userRoleUpdated==1)?"Successfully":"Unsuccessfully")
                + " tried to update '" + user.getUsername() + "' role_id to " + role + " .");

        return userRoleUpdated;

    }

    public int updateUserFLName(User user, String name, String field) {
        int nameUpdated = 0;

        String query = "UPDATE `users` SET `" + field + "` = '"+ name + "' WHERE `id` = '" + user.getID() + "';";
        nameUpdated = execUpdateInsert(query);

        //Log the activity
        ChatLog.logAcivity( ((nameUpdated==1)?"Successfully":"Unsuccessfully")
                + " tried to update '" + user.getUsername() + "' " + field + " to " + name + " .");

        return nameUpdated;
    }

    public int updatePass(User user, String newPass) {
        int passUpdated = 0;

        String query = "UPDATE `users` SET `pass` = '"+ newPass + "' WHERE `id` = '" + user.getID() + "';";
        passUpdated = execUpdateInsert(query);

        //Log the activity
        ChatLog.logAcivity( ((passUpdated==1)?"Successfully":"Unsuccessfully")
                + " tried to update '" + user.getUsername() + " password ");

        return passUpdated;
    }






}
