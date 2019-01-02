package ChatApp.app.db;

import ChatApp.app.core.Chat;
import ChatApp.app.core.User;
import ChatApp.app.db.dbcore.Database;
import ChatApp.app.log.ChatLog;

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
                st.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Log the activity
        ChatLog.logAcivity( ((chatRowInserted==1)?"successfully":"unsuccessfully")
                + " tried to create a new chat with id: " +chat.getId() + " and name: " + chat.getChatName() +" .");

        return chatRowInserted;
    }

    public int deleteChat(Chat chat) {

        int chatsDeleted = 0;
        String queryDeleteMsgs = "DELETE FROM `msgs` WHERE `chat_id` = '" + chat.getId() + "';";
        String queryDeleteChatsUsers = "DELETE FROM `chats_users` WHERE `chat_id` = '" + chat.getId() + "';";
        String queryDeleteChat = "DELETE FROM `chats` WHERE `id` = '" + chat.getId() + "';";

        execUpdateInsert(queryDeleteMsgs);
        if (execUpdateInsert(queryDeleteChatsUsers) >0){
            chatsDeleted =  execUpdateInsert(queryDeleteChat);
        }

        //Log the activity
        ChatLog.logAcivity( ((chatsDeleted==1)?"Successfully":"Unsuccessfully")
                + " deleted chat with id " + chat.getId() + " and name '" + chat.getChatName() + "' .");

        return chatsDeleted;
    }
}

