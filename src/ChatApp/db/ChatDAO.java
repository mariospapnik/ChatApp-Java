package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ChatDAO extends Database {

    public static ChatDAO chatDAO = null;

    private ChatDAO(){}

    public static ChatDAO getInstance() {
        if (chatDAO == null)
            chatDAO = new ChatDAO();
        return chatDAO;
    }

    public void readUserChats(User curUser){

        String query = new StringBuilder()
                .append("SELECT `chats`.`id`,")
                .append(" `chats`.`title` , ")
                .append(" `chats`.`user_id` FROM `chats`")
                .append("INNER JOIN `chats_users`\n")
                .append( "ON `chats`.`id` = `chats_users`.`chat_id`\n")
                .append("WHERE `chats_users`.`user_id`= '" + curUser.getID() + "';").toString();

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        curUser.getChats().clear();
        for (Map<String,Object> row: answer){
            Chat chat = new Chat( (Integer) row.get("id") );
            chat.setChatName( (String) row.get("title") );
            chat.setCreatorUserID( (Integer) row.get("user_id") );

            curUser.getChats().add(chat);
        }

    }


    public void readUsersOfChats(User curUser) {
        curUser.getChats().forEach((chat) -> readUsersOfChat(chat) );
    }

    public void readUsersOfChat(Chat chat){

        //SELECT USERS OF THE CHAT
        String queryChatUsers = new StringBuilder()
                .append("SELECT DISTINCT `user_id` FROM `chats_users` ")
                .append("WHERE `chat_id` = " + chat.getId() + ";").toString();

//        String queryChatUsers = new StringBuilder()
//                .append("SELECT DISTINCT `users`.`id` FROM `chats_users` ")
//                .append("INNER JOIN `users` ON `chats_users`.`chat_id` = " + chat.getId() + ";").toString();

        Collection<Map<String,Object>> answerChatUsers = new ArrayList<>();
        answerChatUsers = getGenericSelect(queryChatUsers);

        chat.getUsersIDs().clear();

        for (Map<String,Object> row: answerChatUsers){
            chat.getUsersIDs().add((Integer) row.get("user_id"));
        }

    }


    public int createChat(Chat chat) {

        Connection conn = createConnection();
        PreparedStatement prest = null;
        Statement st = null;
        ResultSet rs = null;

        int chatRowInserted = 0;
        String chatInsertQuery =  "INSERT INTO `chats` (`title`,`user_id`)"+
                "VALUES (?,?);";

        String chatSelectQuery = "SELECT * FROM `chats` ORDER BY `id` DESC LIMIT 1;";

        String chatsUsersQuery =  "INSERT INTO `chats_users` (`chat_id`,`user_id`)"+
                "VALUES (?,?);";

        try {
            //INSERT NEW ROW IN CHAT TABLE
            prest = conn.prepareStatement(chatInsertQuery);
            prest.setString(1,chat.getChatName());
            prest.setInt(2,chat.getCreatorUserID());

            chatRowInserted = prest.executeUpdate();
            prest.close();

            if (chatRowInserted==1) {
                //GET THE LATEST ID FROM CHAT TABLE
                st = conn.createStatement();
                rs = st.executeQuery(chatSelectQuery);
                rs.first();
                chat.setId(rs.getInt("id"));
                rs.close();

                //INSERT THE RELATION BETWEEN CREATOR_USER AND CHAT INTO CHATS_USERS TABLE
                prest = conn.prepareStatement(chatsUsersQuery);
                prest.setInt(1, chat.getId() );
                prest.setInt(2, chat.getCreatorUserID() );

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatRowInserted;
    }
}


//
//    //SELECT USERS OF THE CHAT
//    String queryChatUsers = new StringBuilder()
//            .append("SELECT DISTINCT `users`.`id` FROM `chat02`.`chat_users` ")
//            .append("INNER JOIN `chat02`.`users` ON `chat_users`.`chat_id` = " + chat.getId() + ";").toString();
//
//    Collection<Map<String,Object>> answerChatUsers = new ArrayList<>();
//        answerChatUsers = getGenericSelect(queryChatUsers);
//
//                for (Map<String,Object> row: answerChatUsers){
//        chat.getUsersIDs().add((Integer) row.get("id"));
//        }
