package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.Role;
import ChatApp.core.User;
import ChatApp.core.Msg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DAOall extends Database {

    public DAOall(){
        registerDriver();
        createURL();
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
            "WHERE `id` = '" + curUser.id + "';";

    //Execute the query and Get the ArrayList of maps (the ROWS!)
    Collection<Map<String,Object>> answer = new ArrayList<>();
    answer = getGenericSelect(query);

    //Loop through the rows and get values
    for (Map<String,Object> row: answer) {
        curUser.id = (Integer) row.get("id");
        curUser.username = (String) row.get("username");
        curUser.roleID = (Integer) row.get("role_id");
    }

}

public Role readRole(Role role){

    String query =  "SELECT * FROM `roles` "+
                    "WHERE `id` = '" + role.id+ "';";

    Collection<Map<String,Object>> answer = new ArrayList<>();
    answer = getGenericSelect(query);

    //Loop through the rows and get values
    for (Map<String,Object> row: answer){
        role.name = row.get("name").toString();
        System.out.println(row.get("can_create_user").getClass());
        role.canCreateUser = (1==(Integer)row.get("can_create_user"));
        role.canCreateChat = (1==(Integer)row.get("can_create_chat"));
        role.canSendMsg = (1==(Integer)row.get("can_send_msg"));
        role.canReadMsg = (1==(Integer)row.get("can_view_msgs"));
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

       String query = new StringBuilder()
               .append("SELECT `chats`.`id`,`chats`.`name`,`chats`.`tb_name` FROM `chats`\n")
               .append("INNER JOIN `chat_users`\n")
               .append("ON `chats`.`id` = `chat_users`.`chat_id`\n")
               .append("WHERE `chat_users`.`user_id`= '"+ curUser.id + "';").toString();

       Collection<Map<String,Object>> answer = new ArrayList<>();
       answer = getGenericSelect(query);

       for (Map<String,Object> row: answer){
           Chat chat = new Chat(Integer.parseInt(row.get("id").toString()));
           chat.chatName = (String) row.get("name");
           chat.msgTableName = (String) row.get("tb_name");
           curUser.chats.add(chat);
       }

   }

    public void readChat(User curUser, Chat chat){

        String query = new StringBuilder()
                .append("SELECT `chats`.`id`,`chats`.`name`,`chats`.`tb_name` FROM `chats`\n").toString();
        System.out.println("ASDASASDASDADSASD");

        //SELECT MSGS TABLE OF THE CHAT
        String queryMsgs = "SELECT * FROM `chat02`.`"+ chat.msgTableName + "`;";
        System.out.println(chat.msgTableName);
        Collection<Map<String,Object>> answerMsgs = new ArrayList<>();
        answerMsgs = getGenericSelect(queryMsgs);
        System.out.println(answerMsgs.size());

        for (Map<String,Object> row: answerMsgs) {
            Msg msg = new Msg();
            msg.id = (Integer) row.get("id");
            msg.creator = (Integer) row.get("user_id");
//            msg.sentDate = Date.row.get("sent_date");
            msg.data = (String) row.get("msg");
            chat.msgs.add(msg);
            System.out.println("ONE MORE");
        }


        //SELECT USERS OF THE CHAT
        String queryChatUsers = new StringBuilder()
                .append("SELECT DISTINCT `users`.`id`, `users`.`username`, `users`.`role_id` FROM `chat02`.`chat_users` ")
                .append("INNER JOIN `chat02`.`users` ON `chat_users`.`chat_id` = " + chat.id + ";").toString();

        Collection<Map<String,Object>> answerChatUsers = new ArrayList<>();
        answerChatUsers = getGenericSelect(queryChatUsers);

    }


}
