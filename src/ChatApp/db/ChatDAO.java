package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.Msg;
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
                .append("SELECT `chats`.`id`,`chats`.`name`,`chats`.`tb_name` FROM `chats`\n")
                .append("INNER JOIN `chat_users`\n")
                .append("ON `chats`.`id` = `chat_users`.`chat_id`\n")
                .append("WHERE `chat_users`.`user_id`= '"+ curUser.getID() + "';").toString();

        Collection<Map<String,Object>> answer = new ArrayList<>();
        answer = getGenericSelect(query);

        curUser.chats.clear();
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

        //SELECT MSGS TABLE OF THE CHAT
        String queryMsgs = "SELECT * FROM `chat02`.`"+ chat.msgTableName + "`;";
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
                .append("SELECT DISTINCT `users`.`id` FROM `chat02`.`chat_users` ")
                .append("INNER JOIN `chat02`.`users` ON `chat_users`.`chat_id` = " + chat.id + ";").toString();

        Collection<Map<String,Object>> answerChatUsers = new ArrayList<>();
        answerChatUsers = getGenericSelect(queryChatUsers);

        for (Map<String,Object> row: answerChatUsers){
            chat.usersIDs.add((Integer) row.get("id"));
        }

    }

    public int createChat(Chat chat) {
        Connection conn = createConnection();
        PreparedStatement prest = null;
        Statement st = null;
        ResultSet rs = null;
        String querySelect = "SELECT * FROM `chats` WHERE `id`=(SELECT MAX(`id`) FROM `chats`);";

        int rowsInserted = 0;
        String query =  "INSERT INTO `chats` (`name`,`user_id`)"+
                "VALUES (?,?);";

        String query2 =  "UPDATE `chats` SET `tb_name` = ? "+
                "WHERE `id` = ?;";

        try {
            prest = conn.prepareStatement(query);
            prest.setString(1,chat.chatName);
//            prest.setString(2,chat.msgTableName);
            prest.setInt(2,chat.creatorUserID);
            rowsInserted = prest.executeUpdate();
            prest.close();

            st = conn.createStatement();
            rs = st.executeQuery(querySelect);
            rs.first();
            int newid = rs.getInt("id");
            chat.msgTableName = "msgs"+newid;
            chat.id = newid;
            rs.close();
            st.close();

            prest = conn.prepareStatement(query2);
            prest.setString(1,chat.msgTableName);
            prest.setInt(2,chat.id);
            prest.executeUpdate();
            prest.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsInserted;
    }
}
