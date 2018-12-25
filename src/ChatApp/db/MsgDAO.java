package ChatApp.db;

import ChatApp.core.Chat;
import ChatApp.core.Msg;
import ChatApp.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class MsgDAO extends Database {

    public static MsgDAO msgDAO = null;

    private MsgDAO(){}

    public static MsgDAO getInstance() {
        if (msgDAO == null)
            msgDAO = new MsgDAO();
        return msgDAO;
    }

    public void readChatMsgs(User curUser, Chat chat){

        //SELECT MSGS TABLE OF THE CHAT
        String queryMsgs = "SELECT * FROM `msgs` WHERE `chat_id` = '"+ chat.getId() + "' ORDER BY `create_date` ASC LIMIT 100;";

        Collection<Map<String,Object>> answerMsgs = new ArrayList<>();
        answerMsgs = getGenericSelect(queryMsgs);

        chat.getMsgs().clear();

        for (Map<String,Object> row: answerMsgs) {
            Msg msg = new Msg();
            msg.setId( (Integer) row.get("id") );
            msg.setCreator( (Integer) row.get("user_id") );
            msg.setSentDate( (Date) row.get("create_date"));
            msg.setData( (String) row.get("data") );

            chat.getMsgs().add(msg);
        }
    }

    public int sendMsg(User curUser, Chat chat, String data) {

        Connection conn = createConnection();
        PreparedStatement prest = null;
        int msgInserted = 0;

        String query = "INSERT INTO `msgs` (`chat_id`, `user_id`,`data`) VALUES (?,?,?) ;";

        try {

            prest = conn.prepareStatement(query);
            prest.setInt(1, chat.getId());
            prest.setInt(2, curUser.getID());
            prest.setString(3, data);

            msgInserted = prest.executeUpdate();
            prest.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return msgInserted;


    }
}

//    public int createMsgTable(Chat chat) {
//
//        Connection conn = createConnection();
//        Statement st = null;
//        int rowsInserted = 0;
//
//        String query = new StringBuilder()
//                .append("CREATE TABLE `"+ chat.msgTableName + "` (")
//                .append("`id` INT NOT NULL AUTO_INCREMENT,\n" +
//                        "    `user_id` INT NOT NULL,\n" +
//                        "    `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
//                        "    `data` VARCHAR(200),\n" +
//                        "    PRIMARY KEY (`id`),\n" +
//                        "    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)\n" +
//                        ");").toString();
//
//        try {
//            st = conn.createStatement();
//            rowsInserted = st.executeUpdate(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return rowsInserted;
//    }
