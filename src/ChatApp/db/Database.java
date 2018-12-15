package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.Role;
import ChatApp.core.User;

import java.sql.*;

public class Database extends DB{

    public Database(){
        registerDriver();
        createURL();
    }

public int checkUser(String username){
    int userID = 0;
    Connection conn = createConnection();
    Statement st = null;
    ResultSet rs = null;
    String query = "SELECT * FROM `users` WHERE `username` = \'" +username + "\';";
    try {
        st = conn.createStatement();
        rs = st.executeQuery(query);
        while(rs.next()){
            userID = rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return userID;
}

public boolean checkPass(int userID, String pass){
    boolean userAuth = false;
    int newID = 0;
    Connection conn = createConnection();
    Statement st = null;
    ResultSet rs = null;
    String query = "SELECT * FROM `users` WHERE `id` = '" +userID + "' AND `pass` = '"+ pass+"';";
    try {
        st = conn.createStatement();
        rs = st.executeQuery(query);
        while(rs.next()){
            newID = rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return newID != 0;
}
public void setUser(User curUser){
    Connection conn = createConnection();
    Statement st = null;
    ResultSet rs = null;
    String query =  "SELECT * FROM `users` "+
            "WHERE `id` = '" + curUser.id + "';";
    try {
        st = conn.createStatement();
        rs = st.executeQuery(query);
        while(rs.next()){
            curUser.id = rs.getInt("id");
            curUser.username = rs.getString("username");
            curUser.roleID = rs.getInt("role_id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public Role readRole(Role role){
    Connection conn = createConnection();
    Statement st = null;
    ResultSet rs = null;
    String query =  "SELECT * FROM `roles` "+
                    "WHERE `id` = '" + role.id+ "';";
    try {
        st = conn.createStatement();
        rs = st.executeQuery(query);

        while(rs.next()){
            role.name = rs.getString(1);
            role.canCreateUser = rs.getBoolean("can_create_user");
            role.canCreateChat = rs.getBoolean("can_create_chat");
            role.canSendMsg = rs.getBoolean("can_send_msg");
            role.canReadMsg = rs.getBoolean("can_view_msgs");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return role;
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

   public void readUserChats(User curUser){
       Connection conn = createConnection();
       PreparedStatement prest = null;
       ResultSet rs = null;

        String query = new StringBuilder()
            .append("SELECT `chats`.`id`,`chats`.`name`,`chats`.`tb_name` FROM `chats`\n")
            .append("INNER JOIN `chat_users`\n")
            .append("ON `chats`.`id` = `chat_users`.`chat_id`\n")
            .append("WHERE `chat_users`.`user_id`= ?;").toString();

       try {
           prest = conn.prepareStatement(query);
           prest.setInt(1,curUser.id);
           rs = prest.executeQuery();
           while(rs.next()){
               Chat chat = new Chat(rs.getInt("id"));
//               System.out.println(rs.getInt("id") + rs.getString("name") + rs.getString("tb_name"));
               chat.chatName = rs.getString("name");
               chat.msgTableName = rs.getString("tb_name");
               curUser.chats.add(chat);
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }


}
