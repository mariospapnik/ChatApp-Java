package ChatApp.app.db;

import ChatApp.app.core.Chat;
import ChatApp.app.core.User;
import ChatApp.app.db.dbcore.Database;
import ChatApp.app.log.ChatLog;

import java.util.ArrayList;

public class ChatUsersDAO  extends Database {

    public static ChatUsersDAO chatUsersDao = null;

    private ChatUsersDAO(){}

    public static ChatUsersDAO getInstance() {
        if (chatUsersDao == null)
            chatUsersDao = new ChatUsersDAO();
        return chatUsersDao;
    }

    public int insertChatUsers(Chat chat, ArrayList<User> usersToInsert){
        int rowsInserted = 0;
        String table = "chats_users";
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String[]> values = new ArrayList<>();

        fields.add("chat_id");
        fields.add("user_id");

        for (User user: usersToInsert) {
            String[] rowValues = {String.valueOf(chat.getId()), String.valueOf(user.getID())};
            values.add(rowValues);
        }

        rowsInserted = genericInsert(table, fields,  values);

        //Log the activity
//        ChatLog.logAcivity( ((rowsInserted==1)?"successfully":"unsuccessfully")
//                + " tried to create a new chat with id: " +chat.getId() + " and name: " + chat.getChatName() +" .");

        return rowsInserted;
    }

    public int deleteChatUsers(Chat chat, ArrayList<User> usersToDelete) {

        int rowsDeleted = 0;
        StringBuilder query = new StringBuilder()
                .append("DELETE FROM `chats_users` WHERE `chat_id` = '" + chat.getId() + "' AND  `user_id` IN (");
        for (int i=0; i<usersToDelete.size(); i++) {

            query.append("'" + usersToDelete.get(i).getID() + "'");
            query.append( (i==usersToDelete.size()-1)? ");" :",");
        }

        rowsDeleted += execUpdateInsert(query.toString());

        //Log the activity
        ChatLog.logAcivity( ((rowsDeleted>0)
                ?"Successfully removed "+ rowsDeleted +" users from chat"
                :"unsuccessfully tried to remove " +usersToDelete.size() )
                + " users from chat with id " +chat.getId() + " and name: " + chat.getChatName() +" .");

        return rowsDeleted;
    }

}
