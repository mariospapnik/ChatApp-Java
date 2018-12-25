package ChatApp.app.db;

import ChatApp.app.core.Chat;
import ChatApp.app.core.User;
import ChatApp.app.db.dbcore.Database;

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
        return rowsInserted;
    }

}